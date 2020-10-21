package gov.nysenate.openleg.updates;

import java.time.LocalDateTime;

public class ContentUpdateEvent
{
    protected LocalDateTime updateDateTime;

    /** --- Constructors --- */

    public ContentUpdateEvent() {
        this(LocalDateTime.now());
    }

    public ContentUpdateEvent(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    /** --- Basic Getters --- */

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }
}