package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.FullMember;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

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
        return memberDao.getAllFullMembers().stream().collect(Collectors.toMap(
                FullMember::getMemberId, fullMember -> fullMember));
    }

    @Override
    protected FullMember getMemberFromDao(Integer memberId) {
        return memberDao.getMemberById(memberId);
    }
}
