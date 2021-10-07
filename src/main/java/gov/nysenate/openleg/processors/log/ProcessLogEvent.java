package gov.nysenate.openleg.processors.log;

public class ProcessLogEvent
{
    private DataProcessUnit log;

    public ProcessLogEvent(DataProcessUnit log) {
        this.log = log;
    }

    public DataProcessUnit getLog() {
        return log;
    }
}
