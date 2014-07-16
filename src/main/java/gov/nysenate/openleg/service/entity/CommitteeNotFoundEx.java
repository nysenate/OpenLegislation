package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.model.entity.Chamber;

import java.util.Date;

public class CommitteeNotFoundEx extends Exception{
    protected String committeeName;
    protected Chamber chamber;
    protected int session;
    protected Date date;

    public CommitteeNotFoundEx(String committeeName, Chamber chamber, int session, Date date, Throwable cause) {
        super(
                "Could not find committee " + chamber + " " + committeeName + " for " + session + " : " + date,
                cause
        );
        this.committeeName = committeeName;
        this.chamber = chamber;
        this.session = session;
        this.date = date;
    }
    public CommitteeNotFoundEx(String committeeName, Chamber chamber,Throwable cause) {
        super(
                "Could not find committee " + chamber + " " + committeeName,
                cause
        );
        this.committeeName = committeeName;
        this.chamber = chamber;
    }
    public CommitteeNotFoundEx(Chamber chamber,Throwable cause) {
        super(
                "Could not find committee records for " + chamber,
                cause
        );
        this.chamber = chamber;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public int getSession() {
        return session;
    }

    public Date getDate() {
        return date;
    }
}
