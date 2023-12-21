package gov.nysenate.openleg.spotchecks.alert.agenda;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Represents committee meeting data as collected from an LRS Alert */
public class AgendaAlertInfoCommittee {
    /** Reference to the report for which this was created */
    private SpotCheckReferenceId referenceId;

    /** The starting day of the week of the referenced agenda */
    private LocalDate weekOf;

    /** A version identifying the addendum of this committee meeting */
    private Version addendum;

    /** Fields found in {@link AgendaInfoCommittee} */
    private CommitteeId committeeId;
    private String chair;
    private String location;
    private LocalDateTime meetingDateTime;
    private String notes;
    private List<AgendaInfoCommitteeItem> items = new ArrayList<>();

    public AgendaAlertInfoCommittee() {}

    public AgendaAlertInfoCommittee(AgendaAlertInfoCommittee other) {
        this.referenceId = other.referenceId;
        this.weekOf = other.weekOf;
        this.addendum = other.addendum;
        this.committeeId = other.committeeId;
        this.chair = other.chair;
        this.location = other.location;
        this.meetingDateTime = other.meetingDateTime;
        this.notes = other.notes;
        this.items = new ArrayList<>(other.items);
    }

    /**
     * Merges two alert info committees, favoring data from the reference with the higher addendum version
     * @param a AgendaAlertInfoCommittee
     * @param b AgendaAlertInfoCommittee
     * @return AgendaAlertInfoCommittee
     */
    public static AgendaAlertInfoCommittee merge(AgendaAlertInfoCommittee a, AgendaAlertInfoCommittee b) {
        if (a == null) {
            return b;
        }
        if (a.committeeId == null || !a.committeeId.equals(b.committeeId) ||
                a.meetingDateTime == null || b.meetingDateTime == null ||
                !a.meetingDateTime.toLocalDate().equals(b.meetingDateTime.toLocalDate())) {
            throw new IllegalArgumentException("AgendaAlertInfoCommittees cannot merge if they don't share the same " +
                    "committee and meeting time");
        }
        AgendaAlertInfoCommittee latest = a.addendum.compareTo(b.addendum) >= 0 ? a : b;
        AgendaAlertInfoCommittee prior = a.addendum.compareTo(b.addendum) >= 0 ? b : a;
        AgendaAlertInfoCommittee merged = new AgendaAlertInfoCommittee(latest);

        prior.getItems().forEach(merged::addInfoCommitteeItem);
        latest.getItems().forEach(merged::addInfoCommitteeItem);

        return merged;
    }

    /** --- Functional Getters / Setters --- */

    public AgendaMeetingWeekId getAgendaMeetingWeekId() {
        return new AgendaMeetingWeekId(getMeetingDateTime().getYear(), getWeekOf(),
                getAddendum(), getCommitteeId());
    }

    public ImmutableList<AgendaInfoCommitteeItem> getItems() {
        return ImmutableList.copyOf(items);
    }

    public void addInfoCommitteeItem(AgendaInfoCommitteeItem item) {
        items.add(item);
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

    public void setItems(List<AgendaInfoCommitteeItem> items) {
        this.items = items;
    }
}
