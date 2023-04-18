package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.FullMember;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.notifications.model.NotificationType.BAD_MEMBER_NAME;

@Component
final class FullMemberIdCache extends AbstractMemberCache<Integer, FullMember> {

    public FullMemberIdCache(MemberDao memberDao) {
        super(memberDao);
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.FULL_MEMBER;
    }

    @Override
    public Map<Integer, FullMember> initialEntries() {
        return memberDao.getAllFullMembers().stream()
                .collect(Collectors.toMap(FullMember::getMemberId, Function.identity()));
    }

    @Override
    protected FullMember getMemberFromDao(Integer memberId) {
        return memberDao.getMemberById(memberId);
    }
}
