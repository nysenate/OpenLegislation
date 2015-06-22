package gov.nysenate.openleg.model.spotcheck;

import java.util.Map;

/**
 * Stores a collection of all currently open observations for a particular spotcheck reference type
 */
public class SpotCheckOpenMismatches<ContentKey>
{
    /** Designates the type of report that produced these open observations */
    private SpotCheckRefType spotCheckRefType;

    /** A map of open observations */
    private Map<ContentKey, SpotCheckObservation<ContentKey>> observations;

    /** The total number of rows before pagination */
    private int totalCurrentMismatches;

    /** --- Constructors --- */

    public SpotCheckOpenMismatches(SpotCheckRefType spotCheckRefType,
                                   Map<ContentKey, SpotCheckObservation<ContentKey>> observations,
                                   int totalCurrentMismatches) {
        this.spotCheckRefType = spotCheckRefType;
        this.observations = observations;
        this.totalCurrentMismatches = totalCurrentMismatches;
    }

    /** --- Getters / Setters --- */

    public SpotCheckRefType getSpotCheckRefType() {
        return spotCheckRefType;
    }

    public Map<ContentKey, SpotCheckObservation<ContentKey>> getObservations() {
        return observations;
    }

    public int getTotalCurrentMismatches() {
        return totalCurrentMismatches;
    }
}
