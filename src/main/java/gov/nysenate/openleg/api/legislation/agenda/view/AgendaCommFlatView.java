package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

public record AgendaCommFlatView(AgendaSummaryView agenda, AgendaCommView committee) implements ViewObject {
    public AgendaCommFlatView(Agenda agenda, CommitteeId committeeId, BillDataService billDataService) {
        this(new AgendaSummaryView(agenda), new AgendaCommView(committeeId, agenda, billDataService));
    }

    @Override
    public String getViewType() {
        return "agenda-committee";
    }
}
