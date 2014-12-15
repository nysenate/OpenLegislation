package gov.nysenate.openleg.model.updates;

import java.time.LocalDateTime;

public class UpdateToken<ContentId> {
    ContentId id;
    LocalDateTime updatedDateTime;

    /** --- Constructors --- */

    public UpdateToken(ContentId id, LocalDateTime updatedDateTime) {
        this.id = id;
        this.updatedDateTime = updatedDateTime;
    }

    /** --- Getters --- */

    public ContentId getId() {
        return id;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }
}
