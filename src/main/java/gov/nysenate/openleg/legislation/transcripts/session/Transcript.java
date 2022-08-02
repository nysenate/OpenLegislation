package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.SessionYear;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A Transcript is a written record of a Senate session.
 */
public class Transcript extends BaseLegislativeContent {
    private final TranscriptId id;
    private final LocalDateTime dateTime;
    private final String sessionType, location, text;
    private String filename;

    /** --- Constructors --- */

    public Transcript(TranscriptId id, String filename, String sessionType, String location, String text) {
        this.id = id;
        this.sessionType = sessionType;
        this.dateTime = id.dateTime();
        this.location = location;
        this.text =  text;
        this.filename = filename;
        this.year = this.dateTime.getYear();
        this.session = SessionYear.of(this.year);
    }

    public TranscriptId getId() {
        return id;
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

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transcript that = (Transcript) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(sessionType, that.sessionType) &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(location, that.location) &&
                Objects.equals(text, that.text) &&
                Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, sessionType, dateTime, location, text, filename);
    }
}
