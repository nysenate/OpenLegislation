package gov.nysenate.openleg.model.entity;

import java.io.Serializable;
import java.util.Date;

public class CommitteeVersionId extends CommitteeId implements Serializable
{
    private static final long serialVersionUID = 2679527346305021089L;

    private int session;
    private Date referenceDate;

    public CommitteeVersionId(Chamber chamber, String name, int session, Date referenceDate){
        super(chamber, name);
        if (referenceDate == null) {
            throw new IllegalArgumentException("referenceDate cannot be null!");
        }
        this.session = session;
        this.referenceDate = referenceDate;
    }

    public int getSession() {
        return session;
    }

    public Date getReferenceDate() {
        return referenceDate;
    }

    @Override
    public String toString() {
        return super.toString() + '-' + session + '-' + referenceDate.toString();
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
}
