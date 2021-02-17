package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.SessionYear;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A Transcript is a written record of Senate sessions.
 */
public class Transcript extends BaseLegislativeContent
{
    /** The transcript id. */
    private final TranscriptId transcriptId;

    /** A transcripts session type. */
    private final String sessionType;

    /** The date time of this transcript. */
    private final LocalDateTime dateTime;

    /** Location of meeting recorded in transcript. */
    private final String location;

    /** The raw text of the transcript. */
    private final String text;

    /** The filename of this transcript. */
    private final String filename;

    /** --- Constructors --- */

    public Transcript(TranscriptId transcriptId, String filename, String sessionType, String location, String text) {
        this.transcriptId = transcriptId;
        this.filename = filename;
        this.sessionType = sessionType;
        this.dateTime = transcriptId.getDateTime();
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

    public String getFilename() {
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transcript that = (Transcript) o;
        return Objects.equals(transcriptId, that.transcriptId) &&
                Objects.equals(sessionType, that.sessionType) &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(location, that.location) &&
                Objects.equals(text, that.text) &&
                Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transcriptId, sessionType, dateTime, location, text, filename);
    }
}
