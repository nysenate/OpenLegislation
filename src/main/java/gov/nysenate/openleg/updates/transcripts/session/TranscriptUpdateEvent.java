package gov.nysenate.openleg.updates.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record TranscriptUpdateEvent(Transcript transcript) implements ContentUpdateEvent {}
