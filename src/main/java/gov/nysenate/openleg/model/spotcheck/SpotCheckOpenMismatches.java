package gov.nysenate.openleg.model.spotcheck;

import java.util.Map;
import java.util.Set;

/**
 * Stores a collection of all currently open observations for a particular spotcheck reference type
 */
public class SpotCheckOpenMismatches<ContentKey>
{
    /** Designates the types of reports that produced these open observations */
    private Set<SpotCheckRefType> spotCheckRefTypes;

    /** A map of open observations */
    private Map<ContentKey, SpotCheckObservation<ContentKey>> observations;

    /** The total number of rows before pagination */
    private int totalCurrentMismatches;

    /** --- Constructors --- */

    public SpotCheckOpenMismatches(Set<SpotCheckRefType> spotCheckRefTypes,
                                   Map<ContentKey, SpotCheckObservation<ContentKey>> observations,
                                   int totalCurrentMismatches) {
        this.spotCheckRefTypes = spotCheckRefTypes;
        this.observations = observations;
        this.totalCurrentMismatches = totalCurrentMismatches;
    }

    /** --- Getters / Setters --- */

    public Set<SpotCheckRefType> getSpotCheckRefTypes() {
        return spotCheckRefTypes;
    }

    public Map<ContentKey, SpotCheckObservation<ContentKey>> getObservations() {
        return observations;
    }

    public int getTotalCurrentMismatches() {
        return totalCurrentMismatches;
    }
}
