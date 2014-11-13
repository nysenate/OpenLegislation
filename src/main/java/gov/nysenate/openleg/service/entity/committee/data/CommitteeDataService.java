package gov.nysenate.openleg.service.entity.committee.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.List;

public interface CommitteeDataService
{
    /**
     * Retrieves the most recent information on the for the given committee on the given session year
     * @param committeeSessionId
     * @return Committee
     * */
    public Committee getCommittee(CommitteeSessionId committeeSessionId) throws CommitteeNotFoundEx;

    /**
     * A convenient overload that gets the most recent information for the given committee for the current session year
     * @see #getCommittee(gov.nysenate.openleg.model.entity.CommitteeSessionId)
     * @param committeeId
     * @return
     * @throws CommitteeNotFoundEx
     */
    default public Committee getCommittee(CommitteeId committeeId) throws CommitteeNotFoundEx {
        return getCommittee(new CommitteeSessionId(committeeId, SessionYear.current()));
    }

    /**
     * Retrieves committee information for the specified committee name at a particular time
     * @param committeeVersionId
     * @return Committee
     * */
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws CommitteeNotFoundEx;

    /**
     * Retrieves a list containing all committee ids
     * @return
     */
    public List<CommitteeId> getCommitteeIds();

    /**
     * Gets all session years that contain committee data
     * @return
     */
    public List<SessionYear> getEligibleYears();

    /**
     * Returns a list of committee session ids for every committee and all sessions that contain data
     *
     * @return
     */
    public List<CommitteeSessionId> getAllCommitteeSessionIds();

    /**
     * Retrieves a list containing the most recent version of each committee for the given session year
     * @param chamber
     * @param sessionYear
     *@param limitOffset  @return List<Committee>
     */
    public List<Committee> getCommitteeList(Chamber chamber, SessionYear sessionYear, LimitOffset limitOffset);

    /**
     * A convenient overload that gets the current committee list for the current session year
     * @see #getCommitteeList(gov.nysenate.openleg.model.entity.Chamber, gov.nysenate.openleg.model.base.SessionYear, gov.nysenate.openleg.dao.base.LimitOffset)
     * @param chamber
     * @param limitOffset
     * @return
     */
    default public List<Committee> getCommitteeList(Chamber chamber, LimitOffset limitOffset) {
        return getCommitteeList(chamber, SessionYear.current(), limitOffset);
    }

    /**
     * Gets the total number of committees for the given chamber for the given session year
     * @param chamber
     * @param sessionYear
     * @return
     */
    public int getCommitteeListCount(Chamber chamber, SessionYear sessionYear);

    /**
     * A convenient overload that gets the current committee list count for the current session year
     * @see #getCommitteeListCount(gov.nysenate.openleg.model.entity.Chamber, gov.nysenate.openleg.model.base.SessionYear)
     * @param chamber
     * @return
     */
    default public int getCommitteeListCount(Chamber chamber) {
        return getCommitteeListCount(chamber, SessionYear.current());
    }

    /**
     * Retrieves a list of committee versions for a given committee and session year that occur within the specified date range
     * ordered by  creation date
     * @param committeeSessionId
     * @param limitOffset
     * @param order
     * @return List<Committee>
     */
    public List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId,
                                               LimitOffset limitOffset, SortOrder order) throws CommitteeNotFoundEx;

    /**
     * A convenient overload of the get committee history function that has no limit
     *  and orders the committees in descending order by creation date
     * @see #getCommitteeHistory(gov.nysenate.openleg.model.entity.CommitteeSessionId, gov.nysenate.openleg.dao.base.LimitOffset, gov.nysenate.openleg.dao.base.SortOrder)
     * @param committeeSessionId
     * @return
     * @throws CommitteeNotFoundEx
     */
    default public List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId) throws CommitteeNotFoundEx {
        return getCommitteeHistory(committeeSessionId, LimitOffset.ALL, SortOrder.DESC);
    }

    /**
     * Gets the total number of committee versions for the given committee id
     *
     * @param committeeSessionId@return
     */
    public int getCommitteeHistoryCount(CommitteeSessionId committeeSessionId);

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     * @param committee
     * @param sobiFragment
     */
    public void saveCommittee(Committee committee, SobiFragment sobiFragment);

    /**
     * Deletes all records for a given committee
     * @param committeeId
     */
    public void deleteCommittee(CommitteeId committeeId);
}
