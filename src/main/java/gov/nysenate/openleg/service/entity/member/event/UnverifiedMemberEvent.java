package gov.nysenate.openleg.service.entity.member.event;

import gov.nysenate.openleg.model.entity.SessionMember;

import java.time.LocalDateTime;

/**
 * An event that is propagated when an unverified member is created
 */
public class UnverifiedMemberEvent extends MemberUpdateEvent {
    public UnverifiedMemberEvent(SessionMember member, LocalDateTime updateDateTime) {
        super(member, updateDateTime);
    }
}
