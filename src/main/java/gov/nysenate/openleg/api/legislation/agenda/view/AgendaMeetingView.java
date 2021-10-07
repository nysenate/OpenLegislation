package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;

import java.time.LocalDateTime;

public class AgendaMeetingView implements ViewObject
{
    private String chair;
    private String location;
    private LocalDateTime meetingDateTime;
    private String notes;

    public AgendaMeetingView(AgendaInfoCommittee infoComm) {
        if (infoComm != null) {
            this.chair = infoComm.getChair();
            this.location = infoComm.getLocation();
            this.meetingDateTime = infoComm.getMeetingDateTime();
            this.notes = infoComm.getNotes();
        }
    }

    public AgendaMeetingView(String chair, String location, LocalDateTime meetingDateTime, String notes) {
        this.chair = chair;
        this.location = location;
        this.meetingDateTime = meetingDateTime;
        this.notes = notes;
    }

    //Added for Json Deserialization
    protected AgendaMeetingView() {}

    public String getChair() {
        return chair;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getMeetingDateTime() {
        return meetingDateTime;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String getViewType() {
        return "agenda-meeting";
    }
}
