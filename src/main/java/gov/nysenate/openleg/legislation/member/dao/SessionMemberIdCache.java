package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Map<Integer, SessionMember> initialEntries() {
        return memberDao.getAllSessionMembers(SortOrder.ASC, LimitOffset.ALL).stream()
                .filter(sm -> sm.getMember().isIncumbent())
                .collect(Collectors.toMap(SessionMember::getSessionMemberId, Function.identity()));
    }

    @Override
    protected SessionMember getMemberFromDao(Integer sessionMemberId) {
        return memberDao.getMemberBySessionId(sessionMemberId);
    }
}
