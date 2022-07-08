package gov.nysenate.openleg.search.transcripts.hearing;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.transcripts.hearing.PublicHearingUpdateEvent;
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
public class ElasticPublicHearingSearchService implements PublicHearingSearchService, IndexedSearchService<PublicHearing>
{
    private static final Logger logger = LoggerFactory.getLogger(PublicHearingSearchService.class);

    @Autowired protected OpenLegEnvironment env;
    @Autowired protected EventBus eventBus;
    @Autowired protected ElasticPublicHearingSearchDao publicHearingSearchDao;
    @Autowired protected PublicHearingDataService publicHearingDataService;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(Integer year, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.matchAllQuery(), year, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(String query, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.queryStringQuery(query), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(String query, int year, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.queryStringQuery(query), year, sort, limOff);
    }

    private SearchResults<PublicHearingId> search(QueryBuilder query, Integer year, String sort, LimitOffset limOff)
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
            return publicHearingSearchDao.searchPublicHearings(query, rangeFilter,
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
    public void handlePublicHearingUpdate(PublicHearingUpdateEvent publicHearingUpdateEvent) {
        if (publicHearingUpdateEvent.hearing() != null) {
            updateIndex(publicHearingUpdateEvent.hearing());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(PublicHearing publicHearing) {
        if (env.isElasticIndexing() && publicHearing != null) {
            logger.info("Indexing public hearing {} into elastic search.", publicHearing.getTitle());
            publicHearingSearchDao.updatePublicHearingIndex(publicHearing);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<PublicHearing> publicHearings) {
        if (env.isElasticIndexing() && !publicHearings.isEmpty()) {
            List<PublicHearing> indexablePublicHearings = publicHearings.stream().filter(Objects::nonNull).toList();
            logger.info("Indexing {} public hearings into elastic search.", indexablePublicHearings.size());
            publicHearingSearchDao.updatePublicHearingIndex(indexablePublicHearings);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        publicHearingSearchDao.purgeIndices();
        publicHearingSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        final int bulkSize = 500;
        Queue<PublicHearingId> hearingIdQueue =
                new ArrayDeque<>(publicHearingDataService.getPublicHearingIds(SortOrder.DESC, LimitOffset.ALL));
        while(!hearingIdQueue.isEmpty()) {
            List<PublicHearing> publicHearings = new ArrayList<>(bulkSize);
            for (int i = 0; i < bulkSize && !hearingIdQueue.isEmpty(); i++) {
                PublicHearingId hid = hearingIdQueue.remove();
                publicHearings.add(publicHearingDataService.getPublicHearing(hid));
            }
            updateIndex(publicHearings);
        }
        logger.info("Finished reindexing public hearings.");
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.HEARING)) {
            logger.info("Handling public hearing re-index event.");
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
