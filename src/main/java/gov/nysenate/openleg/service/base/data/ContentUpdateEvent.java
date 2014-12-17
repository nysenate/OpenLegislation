package gov.nysenate.openleg.service.base.data;

import java.time.LocalDateTime;

public class ContentUpdateEvent
{
    protected LocalDateTime updateDateTime;

    /** --- Constructors --- */

    public ContentUpdateEvent(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    /** --- Basic Getters --- */

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }
}