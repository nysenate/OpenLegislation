package gov.nysenate.openleg.search.committee;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.updates.committee.CommitteeUpdateEvent;

public interface CommitteeSearchService {
    SearchResults<CommitteeVersionId> searchAllCommittees(
            boolean currentOnly, String queryStr, String sort, LimitOffset limitOffset
    ) throws SearchException;

    SearchResults<CommitteeVersionId> searchCommittees(
            SessionYear sessionYear, boolean currentOnly, String queryStr, String sort, LimitOffset limitOffset
    ) throws SearchException;

    /**
     * Handles a committee update event by indexing the updated committee
     */
    void handleCommitteeUpdateEvent(CommitteeUpdateEvent committeeUpdateEvent);
}

