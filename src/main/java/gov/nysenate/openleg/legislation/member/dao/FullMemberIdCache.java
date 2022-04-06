package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.FullMember;
import org.springframework.stereotype.Component;

import java.util.List;

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
    protected List<FullMember> getAllMembersFromDao() {
        return memberDao.getAllFullMembers();
    }

    @Override
    protected FullMember getMemberFromDao(Integer memberId) {
        return memberDao.getMemberById(memberId);
    }


    @Override
    protected void putMemberInCache(FullMember member) {
        cache.put(member.getMemberId(), member);
    }
}
