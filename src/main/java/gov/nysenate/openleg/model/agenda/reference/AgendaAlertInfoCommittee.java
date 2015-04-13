package gov.nysenate.openleg.model.agenda.reference;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Represents committee meeting data as collected from an LRS Alert */
public class AgendaAlertInfoCommittee
{
    /** Reference to the report for which this was created */
    private SpotCheckReferenceId referenceId;

    /** The starting day of the week of the referenced agenda */
    private LocalDate weekOf;

    /** A version identifying the addendum of this committee meeting */
    private Version addendum;

    /** Fields found in {@link gov.nysenate.openleg.model.agenda.AgendaInfoCommittee} */
    private CommitteeId committeeId;
    private String chair;
    private String location;
    private LocalDateTime meetingDateTime;
    private String notes;
    private List<AgendaInfoCommitteeItem> items = new ArrayList<>();

    /** --- Functional Getters / Setters --- */

    public ImmutableList<AgendaInfoCommitteeItem> getItems() {
        return ImmutableList.copyOf(items);
    }

    public void addInfoCommitteeItem(AgendaInfoCommitteeItem item) {
        items.add(item);
    }

    public AgendaAlertInfoCommId getAgendaAlertInfoCommId() {
        return new AgendaAlertInfoCommId(referenceId.getRefActiveDateTime(), weekOf, committeeId, addendum);
    }

    /** --- Getters / Setters --- */

    public SpotCheckReferenceId getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(SpotCheckReferenceId referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }

    public void setWeekOf(LocalDate weekOf) {
        this.weekOf = weekOf;
    }

    public Version getAddendum() {
        return addendum;
    }

    public void setAddendum(Version addendum) {
        this.addendum = addendum;
    }

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(CommitteeId committeeId) {
        this.committeeId = committeeId;
    }

    public String getChair() {
        return chair;
    }

    public void setChair(String chair) {
        this.chair = chair;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getMeetingDateTime() {
        return meetingDateTime;
    }

    public void setMeetingDateTime(LocalDateTime meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
