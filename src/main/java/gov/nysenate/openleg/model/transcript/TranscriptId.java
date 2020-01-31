package gov.nysenate.openleg.model.transcript;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.util.DateUtils;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Used to uniquely identify transcripts.
 */
public class TranscriptId implements Serializable, Comparable<TranscriptId>
{
    private static final long serialVersionUID = -6509878885942142022L;

    /** The timestamp which corresponds to the transcript. */
    private Timestamp timestamp = null;
    /** The original string passed in to the constructor. Needed to properly
     * display error when a time cannot be parsed out.*/
    private String passedIn = "";

    /** --- Constructors --- */

    public TranscriptId(Timestamp timestamp) {
        this(timestamp.toString());
    }

    public TranscriptId(LocalDateTime localDateTime) {
        this(localDateTime.toString());
    }

    public TranscriptId(String time) {
        this.passedIn = time;
        try {
            this.timestamp = Timestamp.valueOf(time);
        }
        catch (IllegalArgumentException e) {
            try {
                this.timestamp = DateUtils.toDate(LocalDateTime.parse(time));
            }
            catch (DateTimeParseException e1) {
                // The String was not a time at all.
            }
        }
        // now() will never match a transcript.
        if (this.timestamp == null)
            this.timestamp = Timestamp.valueOf(LocalDateTime.now());
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptId that = (TranscriptId) o;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return timestamp != null ? timestamp.hashCode() : 0;
    }

    @Override
    public int compareTo(TranscriptId o) {
        return ComparisonChain.start()
                .compare(this.timestamp, o.timestamp)
                .result();
    }

    @Override
    public String toString() {
        return "Transcript: " + passedIn;
    }

    /** --- Basic Getters/Setters --- */

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public LocalDateTime getDateTime() {
        return timestamp.toLocalDateTime();
    }
}
