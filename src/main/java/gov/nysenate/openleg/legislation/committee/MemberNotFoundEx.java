package gov.nysenate.openleg.legislation.committee;

import gov.nysenate.openleg.legislation.SessionYear;

import java.io.Serial;

public class MemberNotFoundEx extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1963594118700545358L;

    private String lbdcName;
    private int memberId;
    private int sessionMemberId;
    private SessionYear session;
    private Chamber chamber;

    public MemberNotFoundEx() {
        super();
    }

    public MemberNotFoundEx(int sessionMemberId) {
        super("Member with session member id of " + sessionMemberId + " was not found.");
        this.sessionMemberId = sessionMemberId;
    }

    public MemberNotFoundEx(int memberId, SessionYear session) {
        super("Member with id: " + memberId +
                ( session != null ? " during session year: " + session : "")
                + " was not found!");
        this.memberId = memberId;
    }

    public MemberNotFoundEx(String lbdcName, SessionYear session, Chamber chamber) {
        super(chamber.name() + " member with given LBDC name: " + lbdcName + " for " + session + " was not found!");
        this.lbdcName = lbdcName;
        this.session = session;
        this.chamber = chamber;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getSessionMemberId() {
        return sessionMemberId;
    }

    public String getLbdcName() {
        return lbdcName;
    }

    public SessionYear getSession() {
        return session;
    }

    public Chamber getChamber() {
        return chamber;
    }
}
