package gov.nysenate.openleg.model.process;

import org.slf4j.Logger;

import java.time.LocalDateTime;

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

    /** Any processing exceptions (fatal and/or non-fatal) should be appended here. */
    private StringBuilder errors = new StringBuilder();

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
        this.errors.append(exception).append("\\n");
    }

    public void addException(String errorMessage, Logger logger) {
        this.errors.append(errorMessage).append("\\n");
        logger.error(errorMessage);
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

    public StringBuilder getErrors() {
        return errors;
    }

    public void setErrors(StringBuilder errors) {
        this.errors = errors;
    }
}
