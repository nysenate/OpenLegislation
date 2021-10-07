package gov.nysenate.openleg.spotchecks.sensite.bill;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;

import static gov.nysenate.openleg.spotchecks.sensite.bill.LawObservationType.*;

/**
 * An id class that can be used for several different types of law spotcheck observations.
 * This allows all law mismatches to be included in the same report.
 */
public class LawSpotCheckId implements Comparable<LawSpotCheckId> {

    private final String lawChapter;
    private final String locationId;
    private final LawObservationType obsType;

    /* --- Constructors --- */

    public LawSpotCheckId(@Nonnull LawObservationType obsType, String lawChapter, String locationId) {
        this.lawChapter = lawChapter;
        this.locationId = locationId;
        this.obsType = obsType;
    }

    public static LawSpotCheckId lawTreeId(String chapter) {
        return new LawSpotCheckId(TREE, chapter, null);
    }

    public static LawSpotCheckId allChaptersId() {
        return new LawSpotCheckId(ALL_CHAPTERS, null, null);
    }

    public static LawSpotCheckId lawDocId(String chapter, String locationId) {
        return new LawSpotCheckId(DOCUMENT, chapter, locationId);
    }

    public static LawSpotCheckId statuteId(String statuteId) {
        String chapter = null;
        String locId = null;
        if (statuteId == null || statuteId.length() < 4) {
            chapter = statuteId;
        } else {
            chapter = statuteId.substring(0, 3);
            locId = statuteId.substring(3);
        }
        return lawDocId(chapter, locId);
    }

    /* --- Implementations --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LawSpotCheckId)) return false;
        LawSpotCheckId that = (LawSpotCheckId) o;
        return Objects.equal(lawChapter, that.lawChapter) &&
                Objects.equal(locationId, that.locationId) &&
                obsType == that.obsType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lawChapter, locationId, obsType);
    }

    @Override
    public int compareTo(LawSpotCheckId o) {
        return ComparisonChain.start()
                .compare(this.obsType, o.obsType)
                .compare(this.lawChapter, o.lawChapter)
                .compare(this.locationId, o.locationId)
                .result();
    }

    /* --- Getters --- */

    public String getLawChapter() {
        return lawChapter;
    }

    public String getLocationId() {
        return locationId;
    }

    @Nonnull
    public LawObservationType getObsType() {
        return obsType;
    }
}
