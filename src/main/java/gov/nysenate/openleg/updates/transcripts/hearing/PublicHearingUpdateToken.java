package gov.nysenate.openleg.updates.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;

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
