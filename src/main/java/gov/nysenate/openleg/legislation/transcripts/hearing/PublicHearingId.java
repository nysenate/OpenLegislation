package gov.nysenate.openleg.legislation.transcripts.hearing;

import java.io.Serial;
import java.io.Serializable;

/**
 * Uniquely identifies public hearing objects.
 * @param id The public hearing's file name
 */
public record PublicHearingId(int id) implements Serializable, Comparable<PublicHearingId> {
    @Serial
    private static final long serialVersionUID = -1772963995918679372L;

    @Override
    public int compareTo(PublicHearingId o) {
        return Integer.compare(id, o.id);
    }
}
