package gov.nysenate.openleg.legislation.transcripts.hearing;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Uniquely identifies public hearing objects.
 */
public class PublicHearingId implements Serializable, Comparable<PublicHearingId> {
    @Serial
    private static final long serialVersionUID = -1772963995918679372L;

    /** The public hearing's file name */
    private final String fileName;

    /** --- Constructors --- */

    public PublicHearingId(String fileName) {
        this.fileName = fileName;
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(PublicHearingId o) {
        return Objects.compare(fileName, o.getFileName(), String::compareTo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicHearingId hearingId = (PublicHearingId) o;
        return Objects.equals(fileName, hearingId.fileName);
    }

    @Override
    public int hashCode() {
        return fileName != null ? fileName.hashCode() : 0;
    }

    /** --- Basic Getters/Setters --- */

    public String getFileName() {
        return fileName;
    }
}
