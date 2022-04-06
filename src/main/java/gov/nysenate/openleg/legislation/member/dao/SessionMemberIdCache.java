package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
final class SessionMemberIdCache extends AbstractMemberCache<Integer, SessionMember> {

    public SessionMemberIdCache(MemberDao memberDao) {
        super(memberDao);
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.SESSION_MEMBER;
    }

    @Override
    protected List<SessionMember> getAllMembersFromDao() {
        return memberDao.getAllMembers(SortOrder.ASC, LimitOffset.ALL);
    }

    @Override
    protected SessionMember getMemberFromDao(Integer sessionMemberId) {
        return memberDao.getMemberBySessionId(sessionMemberId);
    }

    @Override
    protected void putMemberInCache(SessionMember member) {
        cache.put(member.getSessionMemberId(), member);
    }
}
