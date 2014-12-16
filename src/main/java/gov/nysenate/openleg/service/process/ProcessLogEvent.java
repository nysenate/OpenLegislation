package gov.nysenate.openleg.service.process;

import gov.nysenate.openleg.model.process.DataProcessUnit;

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
