package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
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
     * Retrieves a list containing the most recent version of each committee
     *
     * @param chamber
     * @return List<Committee>
     */
    public List<Committee> getCommitteeList(Chamber chamber) throws DataAccessException;

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     *
     * @param committeeId
     * @return List<Committee>
     */
    public List<Committee> getCommitteeHistory(CommitteeId committeeId) throws DataAccessException;

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
