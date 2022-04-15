package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

abstract class AbstractMemberCache<Key, Value> extends CachingService<Key, Value> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMemberCache.class);
    protected final MemberDao memberDao;

    @Autowired
    public AbstractMemberCache(MemberDao memberDao) {
        super();
        this.memberDao = memberDao;
    }

    @Override
    public void warmCaches() {
        evictCache();
        getAllMembersFromDao().forEach(this::putMemberInCache);
        logger.info("Warmed up " + cacheType().name() + " cache.");
    }

    public Value getMember(Key key) throws MemberNotFoundEx{
        Value member = getCacheValue(key);
        if (member == null) {
            member = getMemberFromDao(key);
            putMemberInCache(member);
        }
        return member;
    }

    protected abstract void putMemberInCache(Value member);

    /**
     * Pulls the relevant member data from the DAO.
     * @return all members.
     */
    protected abstract List<Value> getAllMembersFromDao();

    /**
     * If the member data is not in the cache, it has to be pulled from the DAO.
     */
    protected abstract Value getMemberFromDao(Key key) throws MemberNotFoundEx;
}
