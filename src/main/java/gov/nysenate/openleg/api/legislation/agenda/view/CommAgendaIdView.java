package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;

public class CommAgendaIdView implements ViewObject {
    private AgendaIdView agendaId;
    private CommitteeIdView committeeId;

    public CommAgendaIdView(){}
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
