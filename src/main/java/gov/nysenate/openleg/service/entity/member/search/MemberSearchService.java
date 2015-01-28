package gov.nysenate.openleg.service.entity.member.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.MemberId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.member.event.BulkMemberUpdateEvent;
import gov.nysenate.openleg.service.entity.member.event.MemberUpdateEvent;


public interface MemberSearchService
{
    /**
     * Provides a listing of all members.
     * @see #searchMembers(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<MemberId> searchMembers(String sort, LimitOffset limOff) throws SearchException;

    /**
     * Provides a listing of members in a session year.
     * @see #searchMembers(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<MemberId> searchMembers(int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all members.
     * @see #searchMembers(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<MemberId> searchMembers(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all members in a given session year.
     *
     * @param query Search query.
     * @param year Filter by session year.
     * @param sort Sort by field(s)
     * @param limOff Restrict the result set.
     * @return
     * @throws SearchException
     */
    public SearchResults<MemberId> searchMembers(String query, int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Handles a member update event by indexing the supplied member.
     * @param memberUpdateEvent
     */
    public void handleMemberUpdate(MemberUpdateEvent memberUpdateEvent);

    /**
     * Handles a batch member update event by indexing the supplied members.
     * @param bulkMemberUpdateEvent
     */
    public void handleBulkMemberUpdate(BulkMemberUpdateEvent bulkMemberUpdateEvent);

}
