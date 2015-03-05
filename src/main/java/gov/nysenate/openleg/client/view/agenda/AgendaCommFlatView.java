package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.service.bill.data.BillDataService;

public class AgendaCommFlatView implements ViewObject
{
    private AgendaSummaryView agenda;
    private AgendaCommView committee;

    public AgendaCommFlatView(Agenda agenda, CommitteeId committeeId, BillDataService billDataService) {
        this.agenda = new AgendaSummaryView(agenda);
        this.committee = new AgendaCommView(committeeId, agenda, billDataService);
    }

    @Override
    public String getViewType() {
        return "agenda-committee";
    }

    public AgendaSummaryView getAgenda() {
        return agenda;
    }

    public AgendaCommView getCommittee() {
        return committee;
    }
}
