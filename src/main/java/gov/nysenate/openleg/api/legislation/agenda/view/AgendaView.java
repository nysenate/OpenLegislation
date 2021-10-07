package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;

import java.util.stream.Collectors;

public class AgendaView extends AgendaSummaryView implements ViewObject
{
    private ListView<AgendaCommView> committeeAgendas;

    public AgendaView(Agenda agenda, BillDataService billDataService) {
        super(agenda);
        if (agenda != null) {
            this.committeeAgendas = ListView.of(agenda.getAgendaInfoAddenda().values().stream()
                .flatMap(ia -> ia.getCommitteeInfoMap().keySet().stream())
                .distinct()
                .map(cid -> new AgendaCommView(cid, agenda, billDataService))
                .collect(Collectors.toList()));
        }
    }

    //Added for Json Deserialization
    protected AgendaView() {}

    public ListView<AgendaCommView> getCommitteeAgendas() {
        return committeeAgendas;
    }

    @Override
    public String getViewType() {
        return "agenda";
    }
}
