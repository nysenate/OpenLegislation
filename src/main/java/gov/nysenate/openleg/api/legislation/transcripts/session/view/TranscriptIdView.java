package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;

public class TranscriptIdView implements ViewObject {
    private final String dateTime;
    private final String sessionType;

    public TranscriptIdView(TranscriptId transcriptId) {
        this.dateTime = transcriptId.dateTime().toString();
        this.sessionType = transcriptId.sessionType();
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getSessionType() {
        return sessionType;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
