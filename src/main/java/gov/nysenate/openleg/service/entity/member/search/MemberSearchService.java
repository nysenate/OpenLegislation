package gov.nysenate.openleg.service.entity.member.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;


public interface MemberSearchService
{
    /**
     * Provides a listing of member ids for members in a session year.
     * @see #searchMembers(String, SessionYear, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    SearchResults<Integer> searchMembers(SessionYear sessionYear, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Provides a listing of member ids for members of a chamber in a session year.
     * @see #searchMembers(String, SessionYear, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    SearchResults<Integer> searchMembers(SessionYear sessionYear, Chamber chamber, String sort, LimitOffset limOff) throws SearchException;


    /**
     * Performs a search across all members.
     * @see #searchMembers(String, SessionYear, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    SearchResults<Integer> searchMembers(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all members in a given session year.
     *
     * @param query Search query.
     * @param sessionYear Filter by session year.
     * @param sort Sort by field(s)
     * @param limOff Restrict the result set.
     * @return
     * @throws SearchException
     */
    SearchResults<Integer> searchMembers(String query, SessionYear sessionYear, String sort, LimitOffset limOff) throws SearchException;
}
