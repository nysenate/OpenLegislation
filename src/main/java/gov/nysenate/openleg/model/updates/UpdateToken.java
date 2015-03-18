package gov.nysenate.openleg.model.updates;

import java.time.LocalDateTime;

/**
 * An UpdateToken indicates the
 * @param <ContentId>
 */
public class UpdateToken<ContentId>
{
    protected ContentId id;
    protected String sourceId;
    protected LocalDateTime sourceDateTime;
    protected LocalDateTime processedDateTime;
    protected UpdateContentType contentType;

    /** --- Constructors --- */

    public UpdateToken(ContentId id, UpdateContentType contentType, String sourceId,
                       LocalDateTime sourceDateTime, LocalDateTime processedDateTime) {
        this.id = id;
        this.contentType = contentType;
        this.sourceId = sourceId;
        this.sourceDateTime = sourceDateTime;
        this.processedDateTime = processedDateTime;
    }

    /** --- Overrides --- */

    public ContentId getId() {
        return id;
    }

    public void setId(ContentId id) {
        this.id = id;
    }

    public UpdateContentType getContentType() {
        return contentType;
    }

    public void setContentType(UpdateContentType contentType) {
        this.contentType = contentType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public LocalDateTime getSourceDateTime() {
        return sourceDateTime;
    }

    public void setSourceDateTime(LocalDateTime sourceDateTime) {
        this.sourceDateTime = sourceDateTime;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(LocalDateTime processedDateTime) {
        this.processedDateTime = processedDateTime;
    }
}
