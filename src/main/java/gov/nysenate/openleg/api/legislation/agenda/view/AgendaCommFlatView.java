package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.agenda.AgendaBillInfo;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.util.List;
import java.util.Map;

public record AgendaCommFlatView(AgendaSummaryView agenda, AgendaCommView committee) implements ViewObject {
    public AgendaCommFlatView(Agenda agenda, CommitteeId committeeId,
                              Map<AgendaInfoCommittee, List<AgendaBillInfo>> infoCommMap) {
        this(new AgendaSummaryView(agenda), new AgendaCommView(committeeId, agenda, infoCommMap));
    }

    @Override
    public String getViewType() {
        return "agenda-committee";
    }
}
