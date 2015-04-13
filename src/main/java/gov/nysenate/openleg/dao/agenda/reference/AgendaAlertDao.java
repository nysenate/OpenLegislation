package gov.nysenate.openleg.dao.agenda.reference;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.agenda.reference.AgendaAlertId;
import gov.nysenate.openleg.model.agenda.reference.AgendaAlertInfoCommId;
import gov.nysenate.openleg.model.agenda.reference.AgendaAlertInfoCommittee;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendaAlertDao {

    /**
     *  Get all agenda alert files that appear in the incoming alerts directory
     * @return List<File>
     */
    List<File> getIncomingAgendaAlerts() throws IOException;

    /**
     * Move the specified agenda alert file to an archive directory
     * @param agendaAlert File
     */
    void archiveAgendaAlert(File agendaAlert) throws IOException;

    /**
     * Get a committee meeting reference fo the given id
     * @param agendaCommInfoId AgendaAlertInfoCommId
     * @return AgendaAlertInfoCommittee
     */
    AgendaAlertInfoCommittee getAgendaAlertInfoCommittee(AgendaAlertInfoCommId agendaCommInfoId);

    /**
     * Get all unchecked Committee meeting references that fall within the given date time range
     * @param dateTimeRange Range<LocalDateTime>
     * @return List<AgendaAlertInfoCommittee>
     */
    List<AgendaAlertInfoCommittee> getUncheckedAgendaAlertReferences(Range<LocalDateTime> dateTimeRange);

    /**
     * Insert/update a comittee meeting reference
     * @param aaic AgendaAlertInfoCommittee
     */
    void updateAgendaAlertInfoCommittee(AgendaAlertInfoCommittee aaic);

    /** Set all committee meeting references that fall under the given agenda alert id as checked */
    void setAgendaAlertChecked(AgendaAlertId agendaAlertId, boolean checked);
}
