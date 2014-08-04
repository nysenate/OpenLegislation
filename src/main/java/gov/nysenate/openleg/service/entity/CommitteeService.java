package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.model.entity.*;

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
     * @return List<Committee>
     */
    public List<Committee> getCommitteeList(Chamber chamber) throws CommitteeNotFoundEx;

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     * @param committeeId
     * @return List<Committee>
     */
    public List<Committee> getCommitteeHistory(CommitteeId committeeId) throws CommitteeNotFoundEx;

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
