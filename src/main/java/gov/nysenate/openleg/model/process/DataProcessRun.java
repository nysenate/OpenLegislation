package gov.nysenate.openleg.model.process;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents basic information about a single data processing job that was run.
 */
public class DataProcessRun
{
    /** Unique id attributed to this run by the backing store. */
    private int processId;

    /** When this data processing run started. */
    private LocalDateTime startDateTime;

    /** When this data processing run ended. */
    private LocalDateTime endDateTime;

    /** Describes how this process run was invoked, e.g. CLI Script, Cron Job */
    private String invokedBy;

    /** Preserve any exception messages here. */
    private StringBuilder exceptions = new StringBuilder();

    /** --- Constructors --- */

    public DataProcessRun() {}

    public DataProcessRun(int processId, LocalDateTime startDateTime, String invokedBy) {
        this.processId = processId;
        this.startDateTime = startDateTime;
        this.invokedBy = invokedBy;
    }

    /** --- Methods --- */

    public void addException(String message) {
        this.exceptions.append(message).append("\\n");
    }

    public void addException(String prefixMessage, Exception ex) {
        if (prefixMessage != null) {
            this.exceptions.append(prefixMessage);
        }
        if (ex != null) {
            this.exceptions.append("\nException: ").append(ex.getMessage())
                .append("\nStack Trace: ").append(ExceptionUtils.getStackTrace(ex));
        }
    }

    /** --- Basic Getters/Setters --- */

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(String invokedBy) {
        this.invokedBy = invokedBy;
    }

    public StringBuilder getExceptions() {
        return exceptions;
    }
}