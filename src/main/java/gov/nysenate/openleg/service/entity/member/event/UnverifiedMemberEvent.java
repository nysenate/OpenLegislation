package gov.nysenate.openleg.service.entity.member.event;

import gov.nysenate.openleg.model.entity.Member;

public class UnverifiedMemberEvent {

    Member member;

    public UnverifiedMemberEvent(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
