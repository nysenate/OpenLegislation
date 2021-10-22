package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;

import java.time.LocalDate;

/**
 * Adds the agenda/committee ids to the meeting view.
 */
public class AgendaMeetingDetailView extends CommAgendaIdView implements ViewObject
{
    protected String addendum;
    protected LocalDate weekOf;
    protected AgendaMeetingView meeting;

    public AgendaMeetingDetailView(CommitteeAgendaId committeeAgendaId, AgendaInfoCommittee infoComm, String addendum,
                                   LocalDate weekOf) {
        super(committeeAgendaId);
        this.meeting = new AgendaMeetingView(getOrDefault(infoComm));
        this.addendum = addendum;
        this.weekOf = weekOf;
    }

    private static AgendaInfoCommittee getOrDefault(AgendaInfoCommittee infoComm) {
        return infoComm == null ? new AgendaInfoCommittee() : infoComm;
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
