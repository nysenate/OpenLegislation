package gov.nysenate.openleg.model.entity;

import java.util.Date;

public class CommitteeVersionId extends CommitteeId implements Comparable<CommitteeVersionId>{
    private int session;
    private Date referenceDate;

    public CommitteeVersionId(Chamber chamber, String name, int session, Date referenceDate){
        super(chamber, name);
        if(referenceDate==null){
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
    public int compareTo(CommitteeVersionId o) {
        return this.referenceDate.compareTo(o.referenceDate);
    }
}
