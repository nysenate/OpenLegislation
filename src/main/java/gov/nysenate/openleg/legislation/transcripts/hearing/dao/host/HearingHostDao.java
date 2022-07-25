package gov.nysenate.openleg.legislation.transcripts.hearing.dao.host;

import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;

import java.util.Set;

public interface HearingHostDao {
    /**
     * Pulls host data from table.
     * @param id to use in the junction table.
     * @return a list of hosts associated with that ID.
     */
    Set<HearingHost> getHearingHosts(HearingId id);

    /**
     * Adds new hosts if needed, and adds new entries to junction table.
     * @param hosts from new hearing.
     */
    void updateHearingHosts(HearingId id, Set<HearingHost> hosts);

    /**
     * Deletes all hosts associated with the given hearing, from both the hearing_host and junction table.
     * @param id of associated hearing.
     */
    void deleteHearingHosts(HearingId id);
}
