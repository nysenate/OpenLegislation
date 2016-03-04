package gov.nysenate.openleg.service.entity.member.event;

import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;

public class MemberUpdateEvent extends ContentUpdateEvent
{
    protected SessionMember member;

    public MemberUpdateEvent(SessionMember member, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.member = member;
    }

    public SessionMember getMember() {
        return member;
    }
}
