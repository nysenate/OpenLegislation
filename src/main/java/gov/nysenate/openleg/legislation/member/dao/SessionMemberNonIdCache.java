package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Used when we do not have a session member id, but have enough information to pick a
 * SessionMember nonetheless.
 */
@Component
class SessionMemberNonIdCache extends AbstractMemberCache<ShortNameKey, SessionMember> {

    public SessionMemberNonIdCache(MemberDao memberDao) {
        super(memberDao);
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.SHORTNAME;
    }

    @Override
    protected List<SessionMember> getAllMembersFromDao() {
        return memberDao.getAllMembers(SortOrder.ASC, LimitOffset.ALL);
    }

    @Override
    protected SessionMember getMemberFromDao(ShortNameKey key) {
        return memberDao.getMemberByShortName(key.lbdcShortName(), key.sessionYear(), key.chamber());
    }

    @Override
    protected void putMemberInCache(SessionMember member) {
        var key = new ShortNameKey(member.getLbdcShortName(), member.getSessionYear(),
                member.getMember().getChamber());
        cache.put(key, member);
    }
}
