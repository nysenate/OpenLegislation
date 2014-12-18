package gov.nysenate.openleg.model.updates;

import java.time.LocalDateTime;

public class UpdateToken<ContentId>
{
    protected ContentId id;
    protected String sourceId;
    protected LocalDateTime sourceDateTime;
    protected LocalDateTime processedDateTime;

    /** --- Constructors --- */

    public UpdateToken(ContentId id, String sourceId, LocalDateTime sourceDateTime, LocalDateTime processedDateTime) {
        this.id = id;
        this.sourceId = sourceId;
        this.sourceDateTime = sourceDateTime;
        this.processedDateTime = processedDateTime;
    }

    /** --- Overrides --- */

    public ContentId getId() {
        return id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public LocalDateTime getSourceDateTime() {
        return sourceDateTime;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }
}
