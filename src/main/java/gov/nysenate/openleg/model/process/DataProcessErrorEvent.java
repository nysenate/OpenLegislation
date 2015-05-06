package gov.nysenate.openleg.model.process;

public class DataProcessErrorEvent
{
    private int processRunId;
    private String message;
    private Exception ex;

    public DataProcessErrorEvent(String message, Exception ex, int processRunId) {
        this.message = message;
        this.ex = ex;
        this.processRunId = processRunId;
    }

    public int getProcessRunId() {
        return processRunId;
    }

    public String getMessage() {
        return message;
    }

    public Exception getEx() {
        return ex;
    }
}
