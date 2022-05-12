package gov.nysenate.openleg.updates.agenda;

import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public class BulkAgendaUpdateEvent extends ContentUpdateEvent {
    private final Collection<Agenda> agendas;

    public BulkAgendaUpdateEvent(Collection<Agenda> agendas) {
        super();
        this.agendas = agendas;
    }

    public Collection<Agenda> getAgendas() {
        return agendas;
    }
}
