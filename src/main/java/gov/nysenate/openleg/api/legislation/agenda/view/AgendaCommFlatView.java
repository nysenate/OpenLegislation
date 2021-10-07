package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;

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
