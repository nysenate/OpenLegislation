package gov.nysenate.openleg.model.transcript;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
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
    private LocalDateTime sessionDateTime;

    /** --- Constructors --- */

    public TranscriptId(LocalDateTime sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    /**
     * Creates a TranscriptId from a string representation of an ISO date time.
     * @param dateTime String in the format of 'yyyy-mm-ddThh:mm:ss'
     * @throws DateTimeParseException if <code>datetime</code> is in an invalid format.
     */
    public TranscriptId(String dateTime) {
        this(LocalDateTime.parse(dateTime));
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptId that = (TranscriptId) o;
        return Objects.equals(sessionDateTime, that.sessionDateTime);
    }

    @Override
    public int hashCode() {
        return sessionDateTime != null ? sessionDateTime.hashCode() : 0;
    }

    @Override
    public int compareTo(TranscriptId o) {
        return ComparisonChain.start()
                .compare(this.sessionDateTime, o.sessionDateTime)
                .result();
    }

    @Override
    public String toString() {
        return "Transcript " + sessionDateTime;
    }

    /** --- Basic Getters/Setters --- */

    public LocalDateTime getSessionDateTime() {
        return sessionDateTime;
    }

    public LocalDateTime getDateTime() {
        return sessionDateTime;
    }
}
