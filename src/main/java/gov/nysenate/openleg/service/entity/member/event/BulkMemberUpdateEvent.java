package gov.nysenate.openleg.service.entity.member.event;

import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkMemberUpdateEvent extends ContentUpdateEvent
{

    protected Collection<Member> members;

    public BulkMemberUpdateEvent(Collection<Member> members, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.members = members;
    }

    public Collection<Member> getMembers() {
        return members;
    }
}
