package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;

import java.time.LocalDateTime;

public record AgendaMeetingView(String chair, String location, LocalDateTime meetingDateTime,
                                String notes) implements ViewObject {
    public AgendaMeetingView(AgendaInfoCommittee infoComm) {
        this(infoComm.getChair(), infoComm.getLocation(), infoComm.getMeetingDateTime(),
                infoComm.getNotes());
    }

    @Override
    public String getViewType() {
        return "agenda-meeting";
    }
}
