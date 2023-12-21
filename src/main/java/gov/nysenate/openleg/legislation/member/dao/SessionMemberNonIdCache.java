package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Used when we do not have a session member id, but have enough information to pick a
 * SessionMember nonetheless.
 */
@Component
final class SessionMemberNonIdCache extends AbstractMemberCache<ShortNameKey, SessionMember> {

    public SessionMemberNonIdCache(MemberDao memberDao) {
        super(memberDao);
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.SHORTNAME;
    }

    @Override
    public Map<ShortNameKey, SessionMember> initialEntries() {
        return memberDao.getAllSessionMembers(SortOrder.ASC, LimitOffset.ALL).stream()
                .filter(sm -> sm.getMember().isIncumbent())
                .collect(Collectors.toMap(ShortNameKey::new, Function.identity()));
    }

    @Override
    protected SessionMember getMemberFromDao(ShortNameKey key) {
        return memberDao.getMemberByShortName(key.lbdcShortName(), key.sessionYear(), key.chamber());
    }
}
