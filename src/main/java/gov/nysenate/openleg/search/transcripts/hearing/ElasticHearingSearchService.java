package gov.nysenate.openleg.search.transcripts.hearing;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchParseException;
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
    private final ElasticHearingSearchDao hearingSearchDao;
    private final HearingDataService hearingDataService;

    @Autowired
    public ElasticHearingSearchService(OpenLegEnvironment env, EventBus eventBus,
                                       ElasticHearingSearchDao hearingSearchDao,
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
    public SearchResults<HearingId> searchHearings(Integer year, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.matchAllQuery(), year, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<HearingId> searchHearings(String query, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.queryStringQuery(query), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<HearingId> searchHearings(String query, int year, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.queryStringQuery(query), year, sort, limOff);
    }

    private SearchResults<HearingId> search(QueryBuilder query, Integer year, String sort, LimitOffset limOff)
            throws SearchException {
        if (limOff == null) {
            limOff = LimitOffset.TEN;
        }
        RangeQueryBuilder rangeFilter = null;
        if (year != null) {
            rangeFilter = QueryBuilders.rangeQuery("date")
                    .from(LocalDate.of(year, 1, 1).toString())
                    .to(LocalDate.of(year, 12, 31).toString());
        }
        try {
            return hearingSearchDao.searchHearings(query, rangeFilter,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex.getMessage(), ex);
        }
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
            hearingSearchDao.updateHearingIndex(hearing);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Hearing> hearings) {
        if (env.isElasticIndexing() && !hearings.isEmpty()) {
            List<Hearing> indexableHearings = hearings.stream().filter(Objects::nonNull).toList();
            logger.info("Indexing {} hearings into elastic search.", indexableHearings.size());
            hearingSearchDao.updateHearingIndex(indexableHearings);
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
