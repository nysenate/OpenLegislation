package gov.nysenate.openleg.service.agenda.event;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

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
