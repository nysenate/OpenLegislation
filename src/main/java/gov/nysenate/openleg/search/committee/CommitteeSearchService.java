package gov.nysenate.openleg.search.committee;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.IndexedSearchService;
import gov.nysenate.openleg.updates.committee.CommitteeUpdateEvent;

public interface CommitteeSearchService extends IndexedSearchService<CommitteeSessionId> {
    /**
     * Searches for the given query across all committee versions
     */
    SearchResults<CommitteeVersionId> searchAllCommittees(
            String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Searches for the given query across only the latest committee versions for all session years
     */
    SearchResults<CommitteeVersionId> searchAllCurrentCommittees(
            String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Searches for the given query across all committee versions for the given session year
     */
    SearchResults<CommitteeVersionId> searchCommitteesForSession(
            SessionYear sessionYear, String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Searches for the given query across all committee versions for the given session year,
     *  filtering the results to only include the most recent committee version for the session year
     */
    SearchResults<CommitteeVersionId> searchCurrentCommitteesForSession(
            SessionYear sessionYear, String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Handles a committee update event by indexing the updated committee
     */
    void handleCommitteeUpdateEvent(CommitteeUpdateEvent committeeUpdateEvent);
}

