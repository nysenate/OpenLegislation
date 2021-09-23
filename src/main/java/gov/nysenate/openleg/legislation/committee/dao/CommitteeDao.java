
package gov.nysenate.openleg.legislation.committee.dao;

import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface CommitteeDao
{
    /**
     * Retrieves the most recent information on the committee designated by name.
     * @param committeeId
     * @return Committee
     */
    Committee getCommittee(CommitteeId committeeId) throws EmptyResultDataAccessException;

    /**
     * Retrieves committee information for the specified committee name at a particular time.
     * @param committeeVersionId
     * @return Committee
     */
    Committee getCommittee(CommitteeVersionId committeeVersionId) throws EmptyResultDataAccessException;

    /**
     * Retrieves a list containing all committee ids.
     * @return List<Committee>
     */
    List<CommitteeId> getCommitteeList();

    /**
     * Retrieves a list of all valid committee session ids.
     * @return
     * @throws DataAccessException
     */
    List<CommitteeSessionId> getAllSessionIds();

    /**
     * Retrieves a list of committee versions for a given committee that occur within the given date range
     * ordered by session year and creation date.
     *
     * @param committeeSessionId
     * @return List<Committee>
     */
    List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId) throws EmptyResultDataAccessException;

    /**
     * Updates a committee with the given new committee version.
     *
     * @param committee
     * @param legDataFragment name is stored in database.
     */
    void updateCommittee(Committee committee, LegDataFragment legDataFragment);

}
