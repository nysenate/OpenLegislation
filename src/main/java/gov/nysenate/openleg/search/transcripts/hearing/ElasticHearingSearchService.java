package gov.nysenate.openleg.search.transcripts.hearing;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Service
public class ElasticHearingSearchService implements HearingSearchService, IndexedSearchService<Hearing> {
    private static final Logger logger = LoggerFactory.getLogger(HearingSearchService.class);

    private final OpenLegEnvironment env;
    private final EventBus eventBus;
    private final SearchDao<HearingId, HearingView, Hearing> hearingSearchDao;
    private final HearingDataService hearingDataService;

    @Autowired
    public ElasticHearingSearchService(OpenLegEnvironment env, EventBus eventBus,
                                       SearchDao<HearingId, HearingView, Hearing> hearingSearchDao,
                                       HearingDataService hearingDataService) {
        this.env = env;
        this.eventBus = eventBus;
        this.hearingSearchDao = hearingSearchDao;
        this.hearingDataService = hearingDataService;
    }

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<HearingId> searchHearings(String query, Integer year, String sort, LimitOffset limOff) throws SearchException {
        return search(IndexedSearchService.getStringQuery(query), year, sort, limOff);
    }

    private SearchResults<HearingId> search(Query query, Integer year, String sort, LimitOffset limOff)
            throws SearchException {
        if (limOff == null) {
            limOff = LimitOffset.TEN;
        }
        if (year != null) {
            var rangeQuery = RangeQuery.of(b -> b.field("date")
                    .from(LocalDate.of(year, 1, 1).toString())
                    .to(LocalDate.of(year, 12, 31).toString()));
            final Query finalQuery = query;
            query = BoolQuery.of(b -> b.must(finalQuery, rangeQuery._toQuery()))._toQuery();
        }
        return hearingSearchDao.searchForIds(query,
                ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleHearingUpdate(HearingUpdateEvent hearingUpdateEvent) {
        if (hearingUpdateEvent.hearing() != null) {
            updateIndex(hearingUpdateEvent.hearing());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Hearing hearing) {
        if (env.isElasticIndexing() && hearing != null) {
            logger.info("Indexing hearing {} into elastic search.", hearing.getTitle());
            hearingSearchDao.updateIndex(hearing);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Hearing> hearings) {
        if (env.isElasticIndexing() && !hearings.isEmpty()) {
            List<Hearing> indexableHearings = hearings.stream().filter(Objects::nonNull).toList();
            logger.info("Indexing {} hearings into elastic search.", indexableHearings.size());
            hearingSearchDao.updateIndex(indexableHearings);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        hearingSearchDao.purgeIndices();
        hearingSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        final int bulkSize = 500;
        Queue<HearingId> hearingIdQueue =
                new ArrayDeque<>(hearingDataService.getHearingIds(SortOrder.DESC, LimitOffset.ALL));
        while(!hearingIdQueue.isEmpty()) {
            List<Hearing> hearings = new ArrayList<>(bulkSize);
            for (int i = 0; i < bulkSize && !hearingIdQueue.isEmpty(); i++) {
                HearingId hid = hearingIdQueue.remove();
                hearings.add(hearingDataService.getHearing(hid));
            }
            updateIndex(hearings);
        }
        logger.info("Finished reindexing hearings.");
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.HEARING)) {
            logger.info("Handling hearing re-index event.");
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.HEARING)) {
            clearIndex();
        }
    }
}
