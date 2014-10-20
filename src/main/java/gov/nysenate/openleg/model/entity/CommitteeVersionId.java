package gov.nysenate.openleg.model.entity;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.SessionYear;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class CommitteeVersionId extends CommitteeId implements Serializable
{
    private static final long serialVersionUID = 2679527346305021089L;

    /** The session year this committee is referenced in. */
    private SessionYear session;

    /** Refers to the date this committee was referenced. */
    private LocalDate referenceDate;

    /** --- Constructors --- */

    public CommitteeVersionId(Chamber chamber, String name, SessionYear session, LocalDate referenceDate) {
        super(chamber, name);
        if (referenceDate == null) {
            throw new IllegalArgumentException("referenceDate cannot be null!");
        }
        this.session = session;
        this.referenceDate = referenceDate;
    }

    public CommitteeVersionId(CommitteeId committeeId, SessionYear session, LocalDate referenceDate) {
        this(committeeId.getChamber(), committeeId.getName(), session, referenceDate);
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return super.toString() + '-' + session + '-' + referenceDate.toString();
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(session, referenceDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        final CommitteeVersionId other = (CommitteeVersionId) obj;
        return Objects.equals(this.session, other.session) &&
               Objects.equals(this.referenceDate, other.referenceDate);
    }

    @Override
    public int compareTo(CommitteeId o) {
        CommitteeVersionId cvId = (CommitteeVersionId) o;
        return ComparisonChain.start()
           .compare(this.referenceDate, cvId.referenceDate)
           .compare(super.getName(), o.getName())
           .compare(super.getChamber(), o.getChamber())
           .result();
    }

    /** --- Basic Getters/Setters --- */

    public SessionYear getSession() {
        return session;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }
}
