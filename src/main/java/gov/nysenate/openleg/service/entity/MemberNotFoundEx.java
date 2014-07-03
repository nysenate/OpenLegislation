package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.model.entity.Chamber;

public class MemberNotFoundEx extends Exception
{
    protected String lbdcName;
    protected int session;
    protected Chamber chamber;

    public MemberNotFoundEx() {
        super();
    }

    public MemberNotFoundEx(String lbdcName, int session, Chamber chamber) {
        super(chamber.name() + " member with given LBDC name: " + lbdcName + " for " + session + " was not found!");
        this.lbdcName = lbdcName;
        this.session = session;
        this.chamber = chamber;
    }

    public String getLbdcName() {
        return lbdcName;
    }

    public int getSession() {
        return session;
    }

    public Chamber getChamber() {
        return chamber;
    }
}
