package gov.nysenate.openleg.client.view.transcript;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.transcript.TranscriptId;

import java.time.LocalDateTime;

public class TranscriptIdView implements ViewObject
{
    private String sessionType;
    private LocalDateTime dateTime;

    public TranscriptIdView(TranscriptId transId) {
        this.sessionType = transId.getSessionType();
        this.dateTime = transId.getDateTime();
    }

    public String getSessionType() {
        return sessionType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "transcript-id";
    }
}
