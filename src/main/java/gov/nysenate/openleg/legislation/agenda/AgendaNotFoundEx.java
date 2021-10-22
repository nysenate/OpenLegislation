package gov.nysenate.openleg.legislation.agenda;

import java.io.Serial;
import java.time.LocalDate;

public class AgendaNotFoundEx extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 2760237573336644451L;

    private AgendaId agendaId;

    public AgendaNotFoundEx(AgendaId agendaId, Throwable cause) {
        super("The agenda with id " + agendaId + " could not be found.", cause);
        this.agendaId = agendaId;
    }

    public AgendaNotFoundEx(AgendaId agendaId) {
        this(agendaId, null);
    }

    public AgendaNotFoundEx(LocalDate weekOf) {
        super("An agenda could not be found for the week of " + weekOf);
    }

    public AgendaNotFoundEx(String message) {
        super(message);
    }

    public AgendaId getAgendaId() {
        return agendaId;
    }
}