package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.Member;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
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
        return memberDao.getAllFullMembers().stream().filter(Member::isIncumbent)
                .collect(Collectors.toMap(FullMember::getMemberId, Function.identity()));
    }

    @Override
    protected FullMember getMemberFromDao(Integer memberId) {
        return memberDao.getMemberById(memberId);
    }
}
