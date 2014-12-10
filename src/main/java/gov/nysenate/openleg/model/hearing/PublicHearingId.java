package gov.nysenate.openleg.model.hearing;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Uniquely identifies public hearing objects.
 */
public class PublicHearingId implements Serializable, Comparable<PublicHearingId>
{

    private static final long serialVersionUID = -1772963995918679372L;

    /** The public hearing's file name */
    private String fileName;

    /** --- Constructors --- */

    public PublicHearingId(String fileName) {
        this.fileName = fileName;
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(PublicHearingId o) {
        return ComparisonChain.start()
                .compare(this.fileName, o.getFileName())
                .result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicHearingId hearingId = (PublicHearingId) o;
        if (fileName != null ? !fileName.equals(hearingId.fileName) : hearingId.fileName != null) return false;

        return true;
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
