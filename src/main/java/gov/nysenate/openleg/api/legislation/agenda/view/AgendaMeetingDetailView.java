package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;

import java.time.LocalDate;

/**
 * Adds the agenda/committee ids to the meeting view.
 */
public class AgendaMeetingDetailView extends CommAgendaIdView implements ViewObject {
    private final String addendum;
    private final LocalDate weekOf;
    private final AgendaMeetingView meeting;

    public AgendaMeetingDetailView(AgendaInfoCommittee infoComm, String addendum,
                                   LocalDate weekOf) {
        super(new CommitteeAgendaId(infoComm.getAgendaId(), infoComm.getCommitteeId()));
        this.meeting = new AgendaMeetingView(infoComm);
        this.addendum = addendum;
        this.weekOf = weekOf;
    }

    @Override
    public String getViewType() {
        return "agenda-meeting-detail";
    }

    public String getAddendum() {
        return addendum;
    }

    public AgendaMeetingView getMeeting() {
        return meeting;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }
}
