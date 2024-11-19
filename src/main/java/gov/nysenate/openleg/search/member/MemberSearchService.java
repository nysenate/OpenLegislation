package gov.nysenate.openleg.search.member;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;


public interface MemberSearchService {
    /**
     * Provides a listing of member ids for members in a session year.
     * @see #searchMembers(String, SessionYear, String, LimitOffset)
     */
    default SearchResults<Integer> searchMembers(SessionYear sessionYear, String sort, LimitOffset limOff)
            throws SearchException {
        return searchMembers(null, sessionYear, sort, limOff);
    }

    /**
     * Provides a listing of member ids for members of a chamber in a session year.
     * @see #searchMembers(String, SessionYear, String, LimitOffset)
     */
    SearchResults<Integer> searchMembers(SessionYear sessionYear, Chamber chamber, String sort, LimitOffset limOff)
            throws SearchException;


    /**
     * Performs a search across all members.
     * @see #searchMembers(String, SessionYear, String, LimitOffset)
     */
    default SearchResults<Integer> searchMembers(String queryStr, String sort, LimitOffset limOff)
            throws SearchException {
        return searchMembers(queryStr, null, sort, limOff);
    }

    /**
     * Performs a search across all members in a given session year.
     * @param query Search query.
     * @param sessionYear Filter by session year.
     * @param sort Sort by field(s)
     * @param limOff Restrict the result set.
     */
    SearchResults<Integer> searchMembers(String query, SessionYear sessionYear, String sort, LimitOffset limOff)
            throws SearchException;
}
