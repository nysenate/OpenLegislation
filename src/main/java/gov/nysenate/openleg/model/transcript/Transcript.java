package gov.nysenate.openleg.model.transcript;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;

import java.time.LocalDateTime;

/**
 * A Transcript is a written record of Senate sessions.
 */
public class Transcript extends BaseLegislativeContent
{
    /** The transcript id. */
    private TranscriptId id;

    /** Location of meeting recorded in transcript. */
    private String location;

    /** The raw text of the transcript. */
    private String transcriptText;

    /** --- Constructors --- */

    public Transcript(TranscriptId transcriptId) {
        this.id = transcriptId;
        this.year = transcriptId.getYear();
        this.session = SessionYear.of(this.getYear());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTranscriptText() {
        return transcriptText;
    }

    public void setTranscriptText(String transcriptText) {
        this.transcriptText = transcriptText;
    }

    public String getSessionType() {
        return id.getSessionType();
    }

    public LocalDateTime getDateTime() {
        return id.getDateTime();
    }
}
