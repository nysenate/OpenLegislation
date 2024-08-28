package gov.nysenate.openleg.search.committee;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.committee.CommitteeUpdateEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticCommitteeSearchService extends IndexedSearchService<Committee> implements CommitteeSearchService {
    private final ElasticCommitteeSearchDao committeeSearchDao;
    private final CommitteeDataService committeeDataService;

    public ElasticCommitteeSearchService(ElasticCommitteeSearchDao committeeSearchDao,
                                         CommitteeDataService committeeDataService, EventBus eventBus) {
        super(committeeSearchDao);
        this.committeeSearchDao = committeeSearchDao;
        this.committeeDataService = committeeDataService;
        eventBus.register(this);
    }

    @Override
    public SearchResults<CommitteeVersionId> searchAllCommittees(
            boolean currentOnly, String queryStr, String sort, LimitOffset limitOffset
    ) throws SearchException {
        return searchCommittees(null, currentOnly, queryStr, sort, limitOffset);
    }

    @Override
    public SearchResults<CommitteeVersionId> searchCommittees(
            SessionYear sessionYear, boolean currentOnly, String queryStr, String sort, LimitOffset limitOffset
    ) throws SearchException {
        var queryBuilder = new BoolQuery.Builder().must(ElasticSearchServiceUtils.getStringQuery(queryStr)._toQuery());
        if (sessionYear != null) {
            queryBuilder.must(getYearQuery("sessionYear", sessionYear.year())._toQuery());
        }
        if (currentOnly) {
            queryBuilder.mustNot(ExistsQuery.of(b -> b.field("reformed"))._toQuery());
        }
        return committeeSearchDao.searchForIds(queryBuilder.build(), sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void handleCommitteeUpdateEvent(CommitteeUpdateEvent committeeUpdateEvent) {
        updateIndex(sessionIdToCommittees(committeeUpdateEvent.committee().getSessionId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildIndex() {
        updateIndex(committeeDataService.getAllCommitteeSessionIds()
                .stream().flatMap(sessionId -> sessionIdToCommittees(sessionId).stream()).toList());
    }

    private List<Committee> sessionIdToCommittees(CommitteeSessionId sessionId) {
        return committeeDataService.getCommitteeHistory(sessionId);
    }
}