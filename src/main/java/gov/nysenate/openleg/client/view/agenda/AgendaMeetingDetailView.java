package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;

/**
 * Adds the agenda/committee ids to the meeting view.
 */
public class AgendaMeetingDetailView extends CommitteeAgendaIdView implements ViewObject
{
    protected AgendaMeetingView meeting;

    public AgendaMeetingDetailView(CommitteeAgendaId committeeAgendaId, AgendaInfoCommittee infoComm) {
        super(committeeAgendaId);
        meeting = new AgendaMeetingView(infoComm);
    }

    @Override
    public String getViewType() {
        return "agenda-meeting-detail";
    }

    public AgendaMeetingView getMeeting() {
        return meeting;
    }
}
