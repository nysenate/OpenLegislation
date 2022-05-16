package gov.nysenate.openleg.updates.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record PublicHearingUpdateEvent(PublicHearing hearing) implements ContentUpdateEvent {}
