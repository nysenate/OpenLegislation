package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;

public class CommAgendaIdView implements ViewObject
{
    private AgendaIdView agendaId;
    private CommitteeIdView committeeId;

    public CommAgendaIdView(CommitteeAgendaId committeeAgendaId) {
        if (committeeAgendaId != null) {
            agendaId = new AgendaIdView(committeeAgendaId.getAgendaId());
            committeeId = new CommitteeIdView(committeeAgendaId.getCommitteeId());
        }
    }

    @Override
    public String getViewType() {
        return "committee-agenda-id";
    }

    public AgendaIdView getAgendaId() {
        return agendaId;
    }

    public CommitteeIdView getCommitteeId() {
        return committeeId;
    }
}
