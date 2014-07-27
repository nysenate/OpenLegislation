package gov.nysenate.openleg.model.entity;

import java.io.Serializable;
import java.util.Date;

public class CommitteeVersionId extends CommitteeId implements Serializable
{
    private static final long serialVersionUID = 2679527346305021089L;

    /** The session year this committee is referenced in. */
    private int session;

    /** Refers to the creation date of this committee version. */
    private Date referenceDate;

    /** --- Constructors --- */

    public CommitteeVersionId(Chamber chamber, String name, int session, Date referenceDate){
        super(chamber, name);
        if (referenceDate == null) {
            throw new IllegalArgumentException("referenceDate cannot be null!");
        }
        this.session = session;
        this.referenceDate = referenceDate;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return super.toString() + '-' + session + '-' + referenceDate.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommitteeVersionId)) return false;
        if (!super.equals(o)) return false;
        CommitteeVersionId that = (CommitteeVersionId) o;
        if (session != that.session) return false;
        if (!referenceDate.equals(that.referenceDate)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + session;
        result = 31 * result + referenceDate.hashCode();
        return result;
    }

    @Override
    public int compareTo(CommitteeId o) {
        int res = super.compareTo(o);
        if (res == 0) {
            CommitteeVersionId cvId = (CommitteeVersionId) o;
            res = this.referenceDate.compareTo(cvId.referenceDate);
        }
        return res;
    }

    /** --- Basic Getters/Setters --- */

    public int getSession() {
        return session;
    }

    public Date getReferenceDate() {
        return referenceDate;
    }
}
