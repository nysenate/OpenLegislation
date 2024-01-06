package gov.nysenate.openleg.legislation.committee.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.processors.bill.LegDataFragment;

import java.util.List;

public interface CommitteeDataService
{
    /**
     * Retrieves the most recent information on the for the given committee on the given session year
     * @param committeeSessionId
     * @return Committee
     * */
    Committee getCommittee(CommitteeSessionId committeeSessionId) throws CommitteeNotFoundEx;

    /**
     * A convenient overload that gets the most recent information for the given committee for the current session year
     * @see #getCommittee(CommitteeSessionId)
     * @param committeeId
     * @return
     * @throws CommitteeNotFoundEx
     */
    default Committee getCommittee(CommitteeId committeeId) throws CommitteeNotFoundEx {
        return getCommittee(new CommitteeSessionId(committeeId, SessionYear.current()));
    }

    /**
     * Retrieves committee information for the specified committee name at a particular time
     * @param committeeVersionId
     * @return Committee
     * */
    Committee getCommittee(CommitteeVersionId committeeVersionId) throws CommitteeNotFoundEx;

    /**
     * Retrieves a list containing all committee ids
     * @return
     */
    List<CommitteeId> getCommitteeIds();

    /**
     * Returns a list of committee session ids for every committee and all sessions that contain data
     *
     * @return
     */
    List<CommitteeSessionId> getAllCommitteeSessionIds();

    /**
     * Retrieves a list containing the most recent version of each committee for the given session year
     * @param chamber
     * @param sessionYear
     *@param limitOffset  @return List<Committee>
     */
    List<Committee> getCommitteeList(Chamber chamber, SessionYear sessionYear, LimitOffset limitOffset);

    /**
     * Gets the total number of committees for the given chamber for the given session year
     * @param chamber
     * @param sessionYear
     * @return
     */
    int getCommitteeListCount(Chamber chamber, SessionYear sessionYear);

    /**
     * Retrieves a list of committee versions for a given committee and session year that occur within the specified date range
     * ordered by  creation date
     * @param committeeSessionId
     * @param limitOffset
     * @param order
     * @return List<Committee>
     */
    List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId,
                                        LimitOffset limitOffset, SortOrder order) throws CommitteeNotFoundEx;

    /**
     * A convenient overload of the get committee history function that has no limit
     *  and orders the committees in descending order by creation date
     * @see #getCommitteeHistory(CommitteeSessionId, LimitOffset, SortOrder)
     * @param committeeSessionId
     * @return
     * @throws CommitteeNotFoundEx
     */
    default List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId) throws CommitteeNotFoundEx {
        return getCommitteeHistory(committeeSessionId, LimitOffset.ALL, SortOrder.DESC);
    }

    /**
     * Gets the total number of committee versions for the given committee id
     *
     * @param committeeSessionId@return
     */
    int getCommitteeHistoryCount(CommitteeSessionId committeeSessionId);

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     * @param committee
     * @param legDataFragment
     */
    void saveCommittee(Committee committee, LegDataFragment legDataFragment);

}
