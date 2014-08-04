package gov.nysenate.openleg.model.agenda;

public class AgendaNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 2760237573336644451L;

    private AgendaId agendaId;

    public AgendaNotFoundEx(AgendaId agendaId) {
        super("The agenda with id " + agendaId + " could not be found.");
    }

    public AgendaNotFoundEx(String message) {
        super(message);
    }

    public AgendaId getAgendaId() {
        return agendaId;
    }
}