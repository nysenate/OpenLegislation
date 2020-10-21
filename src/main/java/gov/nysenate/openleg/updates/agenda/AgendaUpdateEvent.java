package gov.nysenate.openleg.updates.agenda;

import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

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
