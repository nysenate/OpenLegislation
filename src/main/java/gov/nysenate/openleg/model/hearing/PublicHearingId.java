package gov.nysenate.openleg.model.hearing;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Uniquely identifies public hearing objects.
 */
public class PublicHearingId implements Serializable, Comparable<PublicHearingId>
{

    private static final long serialVersionUID = -1772963995918679372L;

    /** The title of the public hearing */
    private String title;

    /** The date time this public hearing was held */
    private LocalDateTime dateTime;

    /** --- Constructors --- */

    public PublicHearingId(String title, LocalDateTime dateTime) {
        this.title = title;
        this.dateTime = dateTime;
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(PublicHearingId o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicHearingId that = (PublicHearingId) o;
        if (dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (dateTime != null ? dateTime.hashCode() : 0);
        return result;
    }

    /** --- Basic Getters/Setters --- */

    protected int getYear() {
        return dateTime.getYear();
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
