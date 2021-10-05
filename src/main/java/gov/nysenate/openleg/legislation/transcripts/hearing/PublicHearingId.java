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
    private final Integer id;

    /** --- Constructors --- */

    public PublicHearingId(Integer id) {
        this.id = id;
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(PublicHearingId o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicHearingId hearingId = (PublicHearingId) o;
        return Objects.equals(id, hearingId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /** --- Basic Getters/Setters --- */

    public Integer getId() {
        return id;
    }
}
