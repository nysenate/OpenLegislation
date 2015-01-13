package gov.nysenate.openleg.service.agenda.event;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;

public class AgendaUpdateEvent extends ContentUpdateEvent
{
    protected Agenda agenda;

    /** --- Constructors --- */

    public AgendaUpdateEvent(Agenda agenda, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.agenda = agenda;
    }

    /** --- Basic Getters --- */

    public Agenda getAgenda() {
        return agenda;
    }
}
