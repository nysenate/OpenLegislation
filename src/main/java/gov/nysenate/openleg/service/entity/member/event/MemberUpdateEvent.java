package gov.nysenate.openleg.service.entity.member.event;

import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;

public class MemberUpdateEvent extends ContentUpdateEvent
{
    protected Member member;

    public MemberUpdateEvent(Member member, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
