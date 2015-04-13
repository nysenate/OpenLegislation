package gov.nysenate.openleg.model.agenda;

import java.time.LocalDate;

public class AgendaNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 2760237573336644451L;

    private AgendaId agendaId;
    private LocalDate weekOf;

    public AgendaNotFoundEx(AgendaId agendaId) {
        super("The agenda with id " + agendaId + " could not be found.");
        this.agendaId = agendaId;
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

    public LocalDate getWeekOf() {
        return weekOf;
    }
}