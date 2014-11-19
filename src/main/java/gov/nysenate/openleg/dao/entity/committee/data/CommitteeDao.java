
package gov.nysenate.openleg.dao.entity.committee.data;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.springframework.dao.DataAccessException;

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
     * Retrieves a list containing all committee ids
     *
     * @return List<Committee>
     */
    public List<CommitteeId> getCommitteeList() throws DataAccessException;

    /**
     * Retrieves a list of all session years with committee data
     * @return
     * @throws DataAccessException
     */
    public List<SessionYear> getEligibleYears() throws DataAccessException;

    /**
     * Retrieves a list of all valid committee session ids
     * @return
     * @throws DataAccessException
     */
    public List<CommitteeSessionId> getAllSessionIds() throws DataAccessException;

    /**
     * Retrieves a list of committee versions for a given committee that occur within the given date range
     * ordered by session year and creation date
     *
     * @param committeeSessionId
     * @return List<Committee>
     */
    public List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId) throws DataAccessException;

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     *
     * @param committee
     * @param sobiFragment
     */
    public void updateCommittee(Committee committee, SobiFragment sobiFragment) throws DataAccessException;

    /**
     * Deletes all records for a given committee
     *
     * @param committeeId
     */
    public void deleteCommittee(CommitteeId committeeId) throws DataAccessException;
}
