package gov.nysenate.openleg.service.entity;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CommitteeService
{
    /**
     * Retrieves the most recent information on the committee designated by name
     * @param committeeId
     * @return Committee
     * */
    public Committee getCommittee(CommitteeId committeeId) throws CommitteeNotFoundEx;
    /**
     * Retrieves committee information for the specified committee name at a particular time
     * @param committeeVersionId
     * @return Committee
     * */
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws CommitteeNotFoundEx;

    /**
     * Retrieves a list containing the most recent version of each committee
     * @param chamber
     * @param limitOffset
     * @return List<Committee>
     */
    public List<Committee> getCommitteeList(Chamber chamber, LimitOffset limitOffset);

    /**
     * Gets the total number of committees for the given chamber
     * @param chamber
     * @return
     */
    public int getCommitteeListCount(Chamber chamber);

    /**
     * Retrieves a list of committee versions for a given committee that occurr within the specified date range
     * ordered by session year and creation date
     * @param committeeId
     * @param dateRange
     * @param limitOffset
     * @param order  @return List<Committee>
     * */
    public List<Committee> getCommitteeHistory(CommitteeId committeeId, Range<LocalDateTime> dateRange,
                                               LimitOffset limitOffset, SortOrder order) throws CommitteeNotFoundEx;

    /**
     * Gets the total number of committee versions for the given committee id
     * @param committeeId
     * @param dateRange
     * @return
     */
    public int getCommitteeHistoryCount(CommitteeId committeeId, Range<LocalDateTime> dateRange);

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     * @param committee
     */
    public void updateCommittee(Committee committee);

    /**
     * Deletes all records for a given committee
     * @param committeeId
     */
    public void deleteCommittee(CommitteeId committeeId);
}
