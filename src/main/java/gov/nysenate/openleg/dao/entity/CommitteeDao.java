
package gov.nysenate.openleg.dao.entity;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

public interface CommitteeDao
{
    /**
     * Retrieves the most recent information on the committee designated by name
     *
     * @param committeeId
     * @return Committee
     */
    public Committee getCommittee(CommitteeId committeeId) throws DataAccessException;

    /**
     * Retrieves committee information for the specified committee name at a particular time
     *
     * @param committeeVersionId
     * @return Committee
     */
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws DataAccessException;

    /**
     * Retrieves a list containing the most recent version of each committee
     *
     * @param chamber
     * @param limitOffset
     * @return List<Committee>
     */
    public List<Committee> getCommitteeList(Chamber chamber, LimitOffset limitOffset) throws DataAccessException;

    /**
     * Gets the total number of committees for the given chamber
     * @param chamber
     * @return
     */
    public int getCommitteeListCount(Chamber chamber);

    /**
     * Retrieves a list of committee versions for a given committee that occur within the given date range
     * ordered by session year and creation date
     *
     * @param committeeId
     * @param dateRange
     * @param limitOffset
     * @param order
     * @return List<Committee>
     */
    public List<Committee> getCommitteeHistory(CommitteeId committeeId, Range<LocalDateTime> dateRange,
                                               LimitOffset limitOffset, SortOrder order) throws DataAccessException;

    /**
     * Gets the total number of committee versions for the given committee id
     * @param committeeId
     * @param dateRange
     * @return
     */
    public int getCommitteeHistoryCount(CommitteeId committeeId, Range<LocalDateTime> dateRange);

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     *
     * @param committee
     */
    public void updateCommittee(Committee committee) throws DataAccessException;

    /**
     * Deletes all records for a given committee
     *
     * @param committeeId
     */
    public void deleteCommittee(CommitteeId committeeId) throws DataAccessException;
}
