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

    /** The filename which contains the transcript. */
    private String filename;

    /** --- Constructors --- */

    public TranscriptId(String filename) {
        this.filename = filename;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranscriptId that = (TranscriptId) o;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return filename != null ? filename.hashCode() : 0;
    }

    @Override
    public int compareTo(TranscriptId o) {
        return ComparisonChain.start()
                .compare(this.filename, o.filename)
                .result();
    }

    @Override
    public String toString() {
        return "Transcript: " + filename;
    }

    /** --- Basic Getters/Setters --- */

    public String getFilename() {
        return filename;
    }
}
