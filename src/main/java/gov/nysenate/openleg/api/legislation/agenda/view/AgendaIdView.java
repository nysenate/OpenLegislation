package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaId;

public record AgendaIdView(long number, int year) implements ViewObject {
    public AgendaIdView(AgendaId agendaId) {
        this(agendaId == null ? 0 : agendaId.getNumber(), agendaId == null ? 0 : agendaId.getYear());
    }

    @Override
    public String getViewType() {
        return "agenda-id";
    }
}
