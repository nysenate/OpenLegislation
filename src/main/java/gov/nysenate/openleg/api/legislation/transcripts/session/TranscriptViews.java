package gov.nysenate.openleg.api.legislation.transcripts.session;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptInfoView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;

/** Builds the transcript views for the listing and search APIs. */
final class TranscriptViews {
    private TranscriptViews() {}

    /** The full view if full is set, otherwise the info view if summary is set, otherwise the id view. */
    static ViewObject of(TranscriptDataService transcriptData, TranscriptId id, boolean summary, boolean full) {
        if (full) {
            return new TranscriptView(transcriptData.getTranscript(id));
        }
        if (summary) {
            return new TranscriptInfoView(transcriptData.getTranscript(id));
        }
        return new TranscriptIdView(id);
    }
}
