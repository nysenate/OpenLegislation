package gov.nysenate.openleg.updates;

import java.time.LocalDateTime;

public abstract class ContentUpdateEvent {
    private final LocalDateTime updateDateTime = LocalDateTime.now();

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }
}