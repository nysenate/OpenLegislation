package gov.nysenate.openleg.model.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by PKS on 4/28/16.
 */
public class SenateSiteAgenda {

    protected LocalDateTime referenceDateTime;
    protected AgendaId agendaId;
    protected LocalDate weekof;
    protected CommitteeId committeeId;
    protected String addendum;
    protected String location;
    protected String notes;
    protected LocalDateTime meetingDateTime;
    protected List<SenateSiteAgendaBill> agendaBills;

    public SenateSiteAgenda(LocalDateTime referenceDateTime) {
        this.referenceDateTime = referenceDateTime;
    }

    /** --- Functional Getters --- */

    public AgendaId getAgendaId(){
        return agendaId;
    }

    public CommitteeAgendaAddendumId getcommitteeAgendaAddendumId(){
        return new CommitteeAgendaAddendumId(agendaId,committeeId, Version.of(addendum));
    }

    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.SENATE_SITE_AGENDA, referenceDateTime);
    }

    /** --- Getters / Setters --- */

    public void setAgendaId(AgendaId agendaId){
        this.agendaId = agendaId;
    }

    public void setCommittee(CommitteeId committeeId){
        this.committeeId = committeeId;
    }

    public void setWeekof(LocalDate weekof){
        this.weekof = weekof;
    }

    public void setAgendaBills(List<SenateSiteAgendaBill> agendaBills){
        this.agendaBills = agendaBills;
    }

    public void setAddendum(String addendum){
        this.addendum = addendum;
    }

    public LocalDate getWeekof(){
        return weekof;
    }

    public CommitteeId getCommitteeId(){
        return committeeId;
    }

    public List<SenateSiteAgendaBill> getAgendaBills(){
        return agendaBills;
    }

    public String getAddendum(){
        return addendum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getMeetingDateTime() {
        return meetingDateTime;
    }

    public void setMeetingDateTime(LocalDateTime meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }
}
