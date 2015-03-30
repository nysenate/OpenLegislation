package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;

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
