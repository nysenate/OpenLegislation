package gov.nysenate.openleg.updates.agenda;

import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public class AgendaUpdateEvent extends ContentUpdateEvent {
    private final Agenda agenda;

    public AgendaUpdateEvent(Agenda agenda) {
        super();
        this.agenda = agenda;
    }

    public Agenda getAgenda() {
        return agenda;
    }
}
