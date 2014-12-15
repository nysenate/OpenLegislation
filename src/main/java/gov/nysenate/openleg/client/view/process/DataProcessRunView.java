package gov.nysenate.openleg.client.view.process;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.process.DataProcessRun;

import java.time.LocalDateTime;

public class DataProcessRunView implements ViewObject
{
    protected int id;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;
    protected String invokedBy;
    protected String exceptions;

    /** --- Constructors --- */

    public DataProcessRunView(DataProcessRun run) {
        if (run != null) {
            this.id = run.getProcessId();
            this.startDateTime = run.getStartDateTime();
            this.endDateTime = run.getEndDateTime();
            this.invokedBy = run.getInvokedBy();
            this.exceptions = run.getExceptions().toString();
        }
    }

    @Override
    public String getViewType() {
        return "data-process-run";
    }

    /** --- Basic Getters --- */

    public int getId() {
        return id;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getInvokedBy() {
        return invokedBy;
    }

    public String getExceptions() {
        return exceptions;
    }
}
