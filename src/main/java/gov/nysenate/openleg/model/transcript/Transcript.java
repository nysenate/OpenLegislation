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
    private TranscriptId transcriptId;

    /** A transcripts session type. */
    private String sessionType;

    /** The date time of this transcript. */
    private LocalDateTime dateTime;

    /** Location of meeting recorded in transcript. */
    private String location;

    /** The raw text of the transcript. */
    private String text;

    /** --- Constructors --- */

    public Transcript(TranscriptId transcriptId, String sessionType, LocalDateTime dateTime, String location, String text) {
        this.transcriptId = transcriptId;
        this.sessionType = sessionType;
        this.dateTime = dateTime;
        this.location = location;
        this.text =  text;
        this.year = this.dateTime.getYear();
        this.session = SessionYear.of(this.year);
    }

    public TranscriptId getTranscriptId() {
        return transcriptId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getText() {
        return text;
    }
}
