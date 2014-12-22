package gov.nysenate.openleg.client.view.source;

import gov.nysenate.openleg.client.view.base.ViewObject;

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
