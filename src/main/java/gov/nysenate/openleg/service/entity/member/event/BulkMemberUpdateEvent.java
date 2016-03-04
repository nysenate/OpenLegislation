package gov.nysenate.openleg.service.entity.member.event;

import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkMemberUpdateEvent extends ContentUpdateEvent
{

    protected Collection<SessionMember> members;

    public BulkMemberUpdateEvent(Collection<SessionMember> members, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.members = members;
    }

    public Collection<SessionMember> getMembers() {
        return members;
    }
}
