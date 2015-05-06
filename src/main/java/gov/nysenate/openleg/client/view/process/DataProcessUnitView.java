package gov.nysenate.openleg.client.view.process;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.process.DataProcessUnit;

import java.time.LocalDateTime;

public class DataProcessUnitView implements ViewObject
{
    protected String sourceType;
    protected String sourceId;
    protected String action;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;
    protected String messages;
    protected String exceptions;

    /** --- Constructors --- */

    public DataProcessUnitView(DataProcessUnit unit) {
        if (unit != null) {
            this.sourceType = unit.getSourceType();
            this.sourceId = unit.getSourceId();
            this.action = unit.getAction().name();
            this.startDateTime = unit.getStartDateTime();
            this.endDateTime = unit.getEndDateTime();
            this.messages = unit.getMessages().toString();
            this.exceptions = unit.getErrorsBuilder().toString();
        }
    }

    @Override
    public String getViewType() {
        return "data-process-unit";
    }

    /** --- Basic Getters --- */

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getMessages() {
        return messages;
    }

    public String getExceptions() {
        return exceptions;
    }
}
