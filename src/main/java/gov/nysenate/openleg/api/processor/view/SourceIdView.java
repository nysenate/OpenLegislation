package gov.nysenate.openleg.api.processor.view;

import gov.nysenate.openleg.api.ViewObject;

import java.time.LocalDateTime;

public class SourceIdView implements ViewObject
{
    protected String sourceType;
    protected String sourceId;
    protected LocalDateTime sourceDateTime;

    public SourceIdView(String sourceType, String sourceId, LocalDateTime sourceDateTime) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.sourceDateTime = sourceDateTime;
    }

    @Override
    public String getViewType() {
        return "source-id";
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public LocalDateTime getSourceDateTime() {
        return sourceDateTime;
    }
}
