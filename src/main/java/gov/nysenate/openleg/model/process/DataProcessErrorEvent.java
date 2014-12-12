package gov.nysenate.openleg.model.process;

public class DataProcessErrorEvent
{
    private String message;
    private Exception ex;

    public DataProcessErrorEvent(String message, Exception ex) {
        this.message = message;
        this.ex = ex;
    }

    public String getMessage() {
        return message;
    }

    public Exception getEx() {
        return ex;
    }
}
