package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;

import java.util.Date;
import java.util.List;

public interface CommitteeService {
    /**
     * Retrieves the most recent information on the committee designated by name
     * @param name
     * @param chamber
     * @return Committee
     * */
    public Committee getCommittee(String name, Chamber chamber) throws CommitteeNotFoundEx;
    /**
     * Retrieves committee information for the specified committee name at a particular time
     * @param name
     * @param chamber
     * @param session
     * @param time
     * @return Committee
     * */
    public Committee getCommittee(String name, Chamber chamber, int session, Date time) throws CommitteeNotFoundEx;

    /**
     * Retrieves a list containing the most recent version of each committee
     * @param chamber
     * @return List<Committee>
     */
    public List<Committee> getCommitteeList(Chamber chamber) throws CommitteeNotFoundEx;

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     * @param name
     * @param chamber
     * @return List<Committee>
     */
    public List<Committee> getCommitteeHistory(String name, Chamber chamber) throws CommitteeNotFoundEx;

    /**
     * Retrieves a list of committee versions for a given committee, ordered from first version to most recent
     * @param committee
     */
    public void updateCommittee(Committee committee);

    /**
     * Deletes all records for a given committee
     * @param committee
     */
    public void deleteCommittee(Committee committee);
}
