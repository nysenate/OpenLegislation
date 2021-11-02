package gov.nysenate.openleg.legislation.committee;

import gov.nysenate.openleg.legislation.SessionYear;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies a committee for a single session of congress
 */
public class CommitteeSessionId extends CommitteeId implements Serializable {
    @Serial
    private static final long serialVersionUID = 8934281581752246522L;

    /** The session year that this committee is active for */
    protected SessionYear session;

    public CommitteeSessionId(Chamber chamber, String name, SessionYear session) {
        super(chamber, name);
        this.session = session;
    }

    public CommitteeSessionId(CommitteeId committeeId, SessionYear session) {
        super(committeeId);
        this.session = session;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return super.toString() + "-" + session.toString();
    }

    @Override
    public int compareTo(CommitteeId o) {
        int superResult = super.compareTo(o);
        if (superResult == 0 && o instanceof CommitteeSessionId) {
            return this.session.compareTo(((CommitteeSessionId) o).getSession());
        }
        return superResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommitteeSessionId that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (session != null ? session.hashCode() : 0);
        return result;
    }

    /** --- Getters / Setters --- */

    public SessionYear getSession() {
        return session;
    }
}
