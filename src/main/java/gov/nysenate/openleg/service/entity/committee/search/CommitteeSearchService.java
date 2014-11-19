package gov.nysenate.openleg.service.entity.committee.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.entity.committee.event.CommitteeUpdateEvent;

public interface CommitteeSearchService extends IndexedSearchService<CommitteeSessionId> {

    /**
     * Searches for the given query across all committee versions
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     */
    public SearchResults<CommitteeVersionId> searchAllCommittees(String query, String sort, LimitOffset limitOffset)
                                                                                        throws SearchException;

    /**
     * Searches for the given query across only the latest committee versions for all session years
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     */
    public SearchResults<CommitteeVersionId> searchAllCurrentCommittees(String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Searches for the given query across all committee versions for the given session year
     * @param sessionYear
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     */
    public SearchResults<CommitteeVersionId> searchCommitteesForSession(SessionYear sessionYear, String query,
                                                                        String sort, LimitOffset limitOffset)
                                                                                        throws SearchException;

    /**
     * Searches for the given query across all committee versions for the given session year,
     *  filtering the results to only include the most recent committee version for the session year
     * @param sessionYear
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     */
    public SearchResults<CommitteeVersionId> searchCurrentCommitteesForSession(SessionYear sessionYear, String query,
                                                                               String sort, LimitOffset limitOffset)
                                                                                        throws SearchException;


    /**
     * Handles a committee update event by indexing the updated committee
     * @param committeeUpdateEvent
     */
    public void handleCommitteeUpdateEvent(CommitteeUpdateEvent committeeUpdateEvent);
}

