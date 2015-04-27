package gov.nysenate.openleg.dao.agenda.reference;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommId;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommittee;
import org.springframework.dao.DataAccessException;

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
    AgendaAlertInfoCommittee getAgendaAlertInfoCommittee(AgendaAlertInfoCommId agendaCommInfoId) throws DataAccessException;

    /**
     * Get all Committee meeting references that fall within the given date time range
     * @param dateTimeRange Range<LocalDateTime>
     * @return List<AgendaAlertInfoCommittee>
     */
    List<AgendaAlertInfoCommittee> getAgendaAlertReferences(Range<LocalDateTime> dateTimeRange);

    /**
     * Get all unchecked Committee meeting references that fall within the given date time range
     * @return List<AgendaAlertInfoCommittee>
     */
    List<AgendaAlertInfoCommittee> getUncheckedAgendaAlertReferences();

    /**
     * Get all Committee meeting references that fall within the given date time range
     * References with the same meeting date and committee name are merged to match 1.9.2 addendum convention
     * @param dateTimeRange Range<LocalDateTime>
     * @return List<AgendaAlertInfoCommittee>
     */
    List<AgendaAlertInfoCommittee> getProdAgendaAlertReferences(Range<LocalDateTime> dateTimeRange);

    /**
     * Get all prod unchecked Committee meeting references that fall within the given date time range
     * References with the same meeting date and committee name are merged to match 1.9.2 addendum convention
     * @return List<AgendaAlertInfoCommittee>
     */
    List<AgendaAlertInfoCommittee> getProdUncheckedAgendaAlertReferences();

    /**
     * Insert/update a comittee meeting reference
     * @param aaic AgendaAlertInfoCommittee
     */
    void updateAgendaAlertInfoCommittee(AgendaAlertInfoCommittee aaic);

    /** Sets the reference corresponding to the given id as checked */
    void setAgendaAlertChecked(AgendaAlertInfoCommId agendaAlertId, boolean checked);

    /** Sets all references with the given committee id and meeting time as prod checked */
    void setAgendaAlertProdChecked(AgendaAlertInfoCommittee alertInfoCommittee, boolean checked);
}
