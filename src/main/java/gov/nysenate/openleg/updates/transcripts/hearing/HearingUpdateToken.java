package gov.nysenate.openleg.updates.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;

import java.time.LocalDateTime;

public record HearingUpdateToken(HearingId hearingId, LocalDateTime dateTime) {}
