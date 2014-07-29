package gov.nysenate.openleg.service.agenda;

public class AgendaNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 2760237573336644451L;

    public AgendaNotFoundEx() {}

    public AgendaNotFoundEx(String message) {
        super(message);
    }
}
