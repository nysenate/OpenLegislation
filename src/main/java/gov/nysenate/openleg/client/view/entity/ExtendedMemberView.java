package gov.nysenate.openleg.client.view.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;


public class ExtendedMemberView extends MemberView {

    protected boolean alternate;
    protected boolean incumbent;

    protected int personId;
    protected String prefix;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String suffix;
    protected String email;
    protected boolean verified;

    protected ExtendedMemberView(){}

    public ExtendedMemberView(SessionMember member) {
        super(member);
        if (member != null) {
            this.alternate = member.isAlternate();
            this.incumbent = member.isIncumbent();
            this.personId = member.getPersonId();
            this.prefix = member.getPrefix();
            this.firstName = member.getFirstName();
            this.middleName = member.getMiddleName();
            this.lastName = member.getLastName();
            this.suffix = member.getSuffix();
            this.verified = member.isVerified();
        }
    }

    @JsonIgnore
    public SessionMember toMember() {
        SessionMember member = new SessionMember();
        member.setMemberId(this.memberId);
        member.setSessionMemberId(this.sessionMemberId);
        member.setLbdcShortName(this.shortName);
        member.setSessionYear(SessionYear.of(this.sessionYear));
        member.setChamber(Chamber.getValue(this.chamber));
        member.setAlternate(this.alternate);
        member.setFullName(fullName);
        member.setDistrictCode(districtCode);
        member.setImgName(imgName);
        member.setAlternate(this.alternate);
        member.setIncumbent(this.alternate);
        member.setPersonId(this.personId);
        member.setPrefix(this.prefix);
        member.setFirstName(this.firstName);
        member.setMiddleName(this.middleName);
        member.setLastName(this.lastName);
        member.setSuffix(this.suffix);
        member.setEmail(this.email);
        member.setVerified(this.verified);
        return member;
    }

    public boolean isAlternate() {
        return alternate;
    }

    public boolean isIncumbent() {
        return incumbent;
    }

    public int getPersonId() {
        return personId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return verified;
    }

    @Override
    public String getViewType() {
        return "member-extended";
    }
}
