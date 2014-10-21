package gov.nysenate.openleg.model.transcript;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Used to uniquely identify transcripts.
 */
public class TranscriptId implements Serializable, Comparable<TranscriptId>
{
    private static final long serialVersionUID = -6509878885942142022L;

    /** The session type in place when this transcript was recorded.
     * e.g. REGULAR SESSION or EXTRAORDINARY SESSION */
    private String sessionType;

    /** The datetime this transcript was recorded */
    private LocalDateTime dateTime;

    /** --- Constructors --- */

    public TranscriptId(String sessionType, LocalDateTime dateTime) {
        this.sessionType = sessionType;
        this.dateTime = dateTime;
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(TranscriptId o) {
        return ComparisonChain.start()
                .compare(this.sessionType, o.sessionType)
                .compare(this.dateTime, o.dateTime)
                .result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptId that = (TranscriptId) o;
        if (dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null) return false;
        if (sessionType != null ? !sessionType.equals(that.sessionType) : that.sessionType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sessionType != null ? sessionType.hashCode() : 0;
        result = 31 * result + (dateTime != null ? dateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Transcript: " + sessionType + " (" + dateTime + ")";
    }

    /** --- Basic Getters/Setters --- */

    protected int getYear() {
        return dateTime.getYear();
    }

    public String getSessionType() {
        return sessionType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
