package gov.nysenate.openleg.search.committee;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.committee.CommitteeUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ElasticCommitteeSearchService implements CommitteeSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticCommitteeSearchService.class);

    private final ElasticCommitteeSearchDao committeeSearchDao;
    private final CommitteeDataService committeeDataService;

    public ElasticCommitteeSearchService(ElasticCommitteeSearchDao committeeSearchDao,
                                         CommitteeDataService committeeDataService,
                                         EventBus eventBus) {
        this.committeeSearchDao = committeeSearchDao;
        this.committeeDataService = committeeDataService;
        eventBus.register(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CommitteeVersionId> searchAllCommittees(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchCommittees(IndexedSearchService.getStringQuery(query), false, sort, limitOffset);
    }

    @Override
    public SearchResults<CommitteeVersionId> searchAllCurrentCommittees(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchCommittees(IndexedSearchService.getStringQuery(query), true, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CommitteeVersionId> searchCommitteesForSession(SessionYear sessionYear, String query,
                                                                        String sort, LimitOffset limitOffset) throws SearchException {
        return searchCommittees(IndexedSearchService.getBasicBoolQuery("sessionYear", sessionYear.year(), query),
                false, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CommitteeVersionId> searchCurrentCommitteesForSession(SessionYear sessionYear, String query,
                                                                               String sort, LimitOffset limitOffset) throws SearchException {
        return searchCommittees(IndexedSearchService.getBasicBoolQuery("sessionYear", sessionYear.year(), query),
                true, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void handleCommitteeUpdateEvent(CommitteeUpdateEvent committeeUpdateEvent) {
        updateIndex(committeeUpdateEvent.committee().getSessionId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateIndex(CommitteeSessionId content) {
        committeeSearchDao.updateCommitteeIndex(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateIndex(Collection<CommitteeSessionId> content) {
        committeeSearchDao.updateCommitteeIndexBulk(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearIndex() {
        committeeSearchDao.purgeIndices();
        committeeSearchDao.createIndices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildIndex() {
        logger.info("Reindexing committees...");
        clearIndex();
        committeeSearchDao.updateCommitteeIndexBulk(committeeDataService.getAllCommitteeSessionIds());
        logger.info("Committee reindex complete.");
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.COMMITTEE)) {
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.COMMITTEE)) {
            clearIndex();
        }
    }

    /** --- Internal Methods --- */

    private static Query getCurrentFilter() {
        return QueryBuilders.bool().mustNot(QueryBuilders.exists().field("reformed").build()._toQuery())
                .build()._toQuery();
    }

    private SearchResults<CommitteeVersionId> searchCommittees(
            Query query, boolean currentOnly, String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        if (currentOnly) {
            final Query finalQuery = query;
            query = BoolQuery.of(b -> b.must(finalQuery, getCurrentFilter()))._toQuery();
        }
        return committeeSearchDao.searchCommittees(query, null,
                ElasticSearchServiceUtils.extractSortBuilders(sort), limitOffset);
    }
}