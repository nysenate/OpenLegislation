package gov.nysenate.openleg.model.spotcheck.senatesite;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SenateSiteDumpSessionId extends SenateSiteDumpId implements Serializable, Comparable<SenateSiteDumpSessionId> {

    private final SessionYear session;

    public SenateSiteDumpSessionId(SpotCheckRefType refType, int fragmentCount, int session) {
        super(refType, fragmentCount);
        this.session = SessionYear.of(session);
    }

    @Override
    public Range<LocalDateTime> getRange() {
        return session.asDateTimeRange();
    }

    @Override
    public int compareTo(SenateSiteDumpSessionId o) {
        return ComparisonChain.start()
                              .compare(this.getRange().lowerEndpoint(), o.getRange().lowerBoundType())
                              .compare(this.getRange().upperEndpoint(), o.getRange().upperEndpoint())
                              .result();
    }

    @Override
    public String toString() {
        return "SenateSiteDumpSessionId{" +
               "session=" + session +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SenateSiteDumpSessionId that = (SenateSiteDumpSessionId) o;

        return !(session != null ? !session.equals(that.session) : that.session != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (session != null ? session.hashCode() : 0);
        return result;
    }
}
