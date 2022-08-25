package gov.nysenate.openleg.search.committee;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.committee.CommitteeUpdateEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchParseException;
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
        return searchCommittees(QueryBuilders.queryStringQuery(query), null, sort, limitOffset);
    }

    @Override
    public SearchResults<CommitteeVersionId> searchAllCurrentCommittees(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchCommittees(QueryBuilders.queryStringQuery(query), getCurrentFilter(), sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CommitteeVersionId> searchCommitteesForSession(SessionYear sessionYear, String query,
                                                                        String sort, LimitOffset limitOffset) throws SearchException {
        return searchCommittees(QueryBuilders.queryStringQuery(query), getSessionFilter(sessionYear), sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CommitteeVersionId> searchCurrentCommitteesForSession(SessionYear sessionYear, String query,
                                                                               String sort, LimitOffset limitOffset) throws SearchException {
        QueryBuilder currentSessionFilter = QueryBuilders.boolQuery()
                .must(getSessionFilter(sessionYear))
                .must(getCurrentFilter());
        return searchCommittees(QueryBuilders.queryStringQuery(query), currentSessionFilter, sort, limitOffset);
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

    QueryBuilder getCurrentFilter() {
        return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("reformed"));
    }

    QueryBuilder getSessionFilter(SessionYear sessionYear) {
        return QueryBuilders.termQuery("sessionYear", sessionYear.year());
    }

    private SearchResults<CommitteeVersionId> searchCommittees(QueryBuilder query, QueryBuilder postFilter,
                                                                     String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        try {
            return committeeSearchDao.searchCommittees(query, postFilter,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limitOffset);
        } catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        } catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex.getMessage(), ex);
        }
    }
}