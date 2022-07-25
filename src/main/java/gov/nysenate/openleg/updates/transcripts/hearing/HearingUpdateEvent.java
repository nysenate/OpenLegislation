package gov.nysenate.openleg.updates.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record HearingUpdateEvent(Hearing hearing) implements ContentUpdateEvent {}
