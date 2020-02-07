package gov.nysenate.openleg.model.transcript;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
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
    private LocalDateTime localDateTime;

    /** --- Constructors --- */

    public TranscriptId(LocalDateTime localDateTime) {
        this(localDateTime.toString());
    }

    public TranscriptId(String time) {
        try {
            this.localDateTime = LocalDateTime.parse(time);
        }
        catch (DateTimeParseException e) {
            // The time may be the String format of a Timestamp.
            this.localDateTime = Timestamp.valueOf(time).toLocalDateTime();
        }
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptId that = (TranscriptId) o;
        return Objects.equals(localDateTime, that.localDateTime);
    }

    @Override
    public int hashCode() {
        return localDateTime != null ? localDateTime.hashCode() : 0;
    }

    @Override
    public int compareTo(TranscriptId o) {
        return ComparisonChain.start()
                .compare(this.localDateTime, o.localDateTime)
                .result();
    }

    @Override
    public String toString() {
        return "Transcript " + localDateTime;
    }

    /** --- Basic Getters/Setters --- */

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public LocalDateTime getDateTime() {
        return localDateTime;
    }
}
