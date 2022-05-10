package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;

abstract class AbstractMemberCache<Key, Value> extends CachingService<Key, Value> {
    protected final MemberDao memberDao;

    @Autowired
    public AbstractMemberCache(MemberDao memberDao) {
        super();
        this.memberDao = memberDao;
    }

    public Value getMember(Key key) throws MemberNotFoundEx{
        Value member = cache.get(key);
        if (member == null) {
            member = getMemberFromDao(key);
            cache.put(key, member);
        }
        return member;
    }

    /**
     * If the member data is not in the cache, it has to be pulled from the DAO.
     */
    protected abstract Value getMemberFromDao(Key key) throws MemberNotFoundEx;
}
