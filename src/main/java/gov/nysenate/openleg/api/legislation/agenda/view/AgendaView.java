package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.agenda.AgendaBillInfo;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;

import java.util.List;
import java.util.Map;

public class AgendaView extends AgendaSummaryView implements ViewObject {
    private ListView<AgendaCommView> committeeAgendas;

    public AgendaView(Agenda agenda, Map<AgendaInfoCommittee, List<AgendaBillInfo>> infoCommMap) {
        super(agenda);
        if (agenda != null) {
            this.committeeAgendas = ListView.of(agenda.getAgendaInfoAddenda().values().stream()
                .flatMap(ia -> ia.getCommitteeInfoMap().keySet().stream())
                .distinct()
                .map(cid -> new AgendaCommView(cid, agenda, infoCommMap)).toList());
        }
    }

    // Added for Json Deserialization
    protected AgendaView() {}

    public ListView<AgendaCommView> getCommitteeAgendas() {
        return committeeAgendas;
    }

    @Override
    public String getViewType() {
        return "agenda";
    }
}
