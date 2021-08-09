package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;

import java.util.List;

public interface HearingHostDao {
    /**
     * Pulls host data from table.
     * @param id to use in the junction table.
     * @return a list of hosts associated with that ID.
     */
    List<HearingHost> getHearingHosts(PublicHearingId id);

    /**
     * Adds new hosts if needed, and adds new entries to junction table.
     * @param hosts from new hearing.
     */
    void updateHearingHosts(PublicHearingId id, List<HearingHost> hosts);

    /**
     * Deletes all hosts associated with the given hearing, from both the hearing_host and junction table.
     * @param id of associated hearing.
     */
    void deleteHearingHosts(PublicHearingId id);
}
