package gov.nysenate.openleg.model.process;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the processing status of a single source file, typically associated with a process run.
 */
public class DataProcessUnit
{
    /** Identifies what kind of source data was processed. */
    private String sourceType;

    /** A source id that in conjunction with the 'sourceType' should uniquely identify the source data. */
    private String sourceId;

    /** The type of processing action that was performed. */
    private DataProcessAction action;

    /** When this source data began processing. */
    private LocalDateTime startDateTime;

    /** When this source data finished processing. */
    private LocalDateTime endDateTime;

    /** Any useful non-fatal messages will be appended here. */
    private StringBuilder messages = new StringBuilder();

    /**
     * Any processing exceptions (fatal and/or non-fatal) should be appended here.
     */
    private List<String> errors = new ArrayList<>();

    /** --- Constructors --- */

    public DataProcessUnit(String sourceType, String sourceId, LocalDateTime startDateTime,
                           DataProcessAction action) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.startDateTime = startDateTime;
        this.action = action;
    }

    /** --- Methods --- */

    public void addMessage(String message) {
        this.messages.append(message).append("\\n");
    }

    public void addException(String exception) {
        errors.add(exception);
    }

    public void addException(String prefixMessage, Exception ex) {
        String message = "";
        if (prefixMessage != null) {
            message = prefixMessage;
        }
        if (ex != null) {
            message = message + "\nException: " + ex.getMessage() + "\nStack Trace: " + ExceptionUtils.getStackTrace(ex);
        }
        errors.add(message);
    }

    public void addException(String errorMessage, Logger logger) {
        if (!errorMessage.endsWith("\n")) {
            errorMessage = errorMessage + "\n";
        }
        errors.add(errorMessage);
        logger.error(errorMessage);
    }
    
    /** --- Functional Getters / Setters --- */

    public StringBuilder getErrorsBuilder() {
        StringBuilder builder = new StringBuilder();
        errors.forEach(builder::append);
        return builder;
    }

    public void setErrors(StringBuilder errors) {
        this.errors = Collections.singletonList(errors.toString());
    }

    /** --- Basic Getters/Setters --- */

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public DataProcessAction getAction() {
        return action;
    }

    public void setAction(DataProcessAction action) {
        this.action = action;
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

    public StringBuilder getMessages() {
        return messages;
    }

    public void setMessages(StringBuilder messages) {
        this.messages = messages;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
