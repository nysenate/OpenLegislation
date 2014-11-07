package gov.nysenate.openleg.model.entity;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.SessionYear;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class CommitteeVersionId extends CommitteeSessionId implements Serializable
{
    private static final long serialVersionUID = 2679527346305021089L;

    /** Refers to the date this committee was referenced. */
    private LocalDateTime referenceDate;

    /** --- Constructors --- */

    public CommitteeVersionId(Chamber chamber, String name, SessionYear session, LocalDateTime referenceDate) {
        super(chamber, name, session);
        if (referenceDate == null) {
            throw new IllegalArgumentException("referenceDate cannot be null!");
        }
        this.referenceDate = referenceDate;
    }

    public CommitteeVersionId(CommitteeId committeeId, SessionYear session, LocalDateTime referenceDate) {
        this(committeeId.getChamber(), committeeId.getName(), session, referenceDate);
    }

    public CommitteeVersionId(CommitteeSessionId committeeSessionId, LocalDateTime referenceDate) {
        this(committeeSessionId.getChamber(), committeeSessionId.getName(), committeeSessionId.getSession(), referenceDate);
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return super.toString() + "-" + referenceDate.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommitteeVersionId)) return false;
        if (!super.equals(o)) return false;

        CommitteeVersionId versionId = (CommitteeVersionId) o;

        if (referenceDate != null ? !referenceDate.equals(versionId.referenceDate) : versionId.referenceDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (referenceDate != null ? referenceDate.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(CommitteeId o) {
        int superResult = super.compareTo(o);
        if (superResult == 0 && o instanceof CommitteeVersionId) {
            return this.referenceDate.compareTo(((CommitteeVersionId) o).getReferenceDate());
        }
        return superResult;
    }

    /** --- Basic Getters/Setters --- */

    public LocalDateTime getReferenceDate() {
        return referenceDate;
    }
}
