package gov.nysenate.openleg.model.base;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An immutable representation of a published/unpublished date as well as some
 * extra metadata that can be set for more complex publishing needs.
 */
public class PublishStatus implements Serializable, Comparable<PublishStatus>
{
    private static final long serialVersionUID = -1038985118047193901L;

    /** Set to true if item is published, false if unpublished. */
    protected boolean published = false;

    /** The date and time when the item was published or unpublished. */
    protected LocalDateTime effectDateTime;

    /** Indicates if the publish status is a result of some form of manual override
     *  as opposed to being set via actual source data. */
    protected boolean override = false;

    /** Any notes that provide some information on the context in which this item
     *  was published or unpublished. For example you could specify some details
     *  regarding why an override was performed. */
    protected String notes = "";

    /** --- Constructors --- */

    public PublishStatus(boolean published, LocalDateTime effectDateTime) {
        this(published, effectDateTime, false, "");
    }

    public PublishStatus(boolean published, LocalDateTime effectDateTime, boolean override, String notes) {
        this.published = published;
        this.effectDateTime = effectDateTime;
        this.override = override;
        this.notes = notes;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PublishStatus other = (PublishStatus) obj;
        return Objects.equals(this.published, other.published) &&
               Objects.equals(this.effectDateTime, other.effectDateTime) &&
               Objects.equals(this.override, other.override) &&
               Objects.equals(this.notes, other.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(published, effectDateTime, override, notes);
    }

    @Override
    public String toString() {
        return ((override) ? "(Override) " : "") +
               ((published) ? "Published" : "Unpublished") + ":" + effectDateTime;
    }

    @Override
    public int compareTo(PublishStatus o) {
        return ComparisonChain.start()
            .compare(this.effectDateTime, o.effectDateTime)
            .compare(this.override, o.override)
            .compare(this.published, o.published)
            .result();
    }

    /** --- Basic Getters --- */

    public boolean isPublished() {
        return published;
    }

    public LocalDateTime getEffectDateTime() {
        return effectDateTime;
    }

    public boolean isOverride() {
        return override;
    }

    public String getNotes() {
        return notes;
    }
}