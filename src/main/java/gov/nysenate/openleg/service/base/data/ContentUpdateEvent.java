package gov.nysenate.openleg.service.base.data;

import java.time.LocalDateTime;

public class ContentUpdateEvent
{
    protected LocalDateTime updateDateTime;

    public ContentUpdateEvent(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }
}