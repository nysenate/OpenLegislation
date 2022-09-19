package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A Transcript is a written record of a Senate session.
 */
public class Transcript extends BaseLegislativeContent {
    private final TranscriptId id;
    private final String location, text, filename;

    /** --- Constructors --- */

    public Transcript(TranscriptId id, String filename, String location, String text) {
        super(id.dateTime().getYear());
        this.id = id;
        this.location = location;
        this.text =  text;
        this.filename = filename;
    }

    public TranscriptId getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return id.dateTime();
    }

    public String getSessionType() {
        return id.sessionType();
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
        return Objects.equals(id, that.id) &&
                Objects.equals(location, that.location) &&
                Objects.equals(text, that.text) &&
                Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, location, text, filename);
    }
}
