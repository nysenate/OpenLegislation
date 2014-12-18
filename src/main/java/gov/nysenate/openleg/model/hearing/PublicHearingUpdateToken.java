package gov.nysenate.openleg.model.hearing;

import java.time.LocalDateTime;

public class PublicHearingUpdateToken
{
    private PublicHearingId publicHearingId;
    private LocalDateTime dateTime;

    public PublicHearingUpdateToken(PublicHearingId publicHearingId, LocalDateTime dateTime) {
        this.publicHearingId = publicHearingId;
        this.dateTime = dateTime;
    }

    public PublicHearingId getPublicHearingId() {
        return publicHearingId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
