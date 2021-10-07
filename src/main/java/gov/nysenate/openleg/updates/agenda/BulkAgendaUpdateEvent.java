package gov.nysenate.openleg.updates.agenda;

import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkAgendaUpdateEvent extends ContentUpdateEvent
{
    protected Collection<Agenda> agendas;

    /** --- Constructors --- */

    public BulkAgendaUpdateEvent(Collection<Agenda> agendas, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.agendas = agendas;
    }

    /** --- Basic Getters --- */

    public Collection<Agenda> getAgendas() {
        return agendas;
    }
}
