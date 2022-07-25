package gov.nysenate.openleg.legislation.transcripts.hearing;

import java.io.Serial;
import java.io.Serializable;

/**
 * Uniquely identifies hearing objects.
 * @param id The hearing's file name
 */
public record HearingId(int id) implements Serializable, Comparable<HearingId> {
    @Serial
    private static final long serialVersionUID = -1772963995918679372L;

    @Override
    public int compareTo(HearingId o) {
        return Integer.compare(id, o.id);
    }
}
