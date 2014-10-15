package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;

import java.time.LocalDateTime;

public class AgendaMeetingView implements ViewObject
{
    private String chair;
    private String location;
    private LocalDateTime meetingDateTime;
    private String notes;

    public AgendaMeetingView(String chair, String location, LocalDateTime meetingDateTime, String notes) {
        this.chair = chair;
        this.location = location;
        this.meetingDateTime = meetingDateTime;
        this.notes = notes;
    }

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
