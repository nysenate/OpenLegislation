package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.ContentCache;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.units.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionMemberIdCache extends CachingService<Integer, SessionMember> {

    private static final Logger logger = LoggerFactory.getLogger(SessionMemberIdCache.class);

    @Value("${member.cache.heap.size}")
    private int memberCacheSizeMb;
    private final MemberDao memberDao;

    @Autowired
    public SessionMemberIdCache(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    protected List<ContentCache> getCacheEnums() {
        return null;
    }

    @Override
    protected boolean isByteSizeOf() {
        return true;
    }

    @Override
    protected int getNumUnits() {
        return memberCacheSizeMb;
    }

    @Override
    protected ResourceUnit getUnit() {
        return MemoryUnit.MB;
    }

    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up Session member cache");
        memberDao.getAllMembers(SortOrder.ASC, LimitOffset.ALL).forEach(this::putMemberInCache);
        logger.info("Done warming up Session member cache");
    }

    /**
     * First checks the cache for the session member, if not there they are retrieved from the database and added to the cache.
     * @param sessionMemberId to lookup in cache.
     * @return the associated session member.
     * @throws MemberNotFoundEx if the sessionMemberId didn't match any session members.
     */
    public SessionMember getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        SessionMember sm = cache.get(sessionMemberId);
        if (sm != null) {
            return sm;
        } else {
            try {
                sm = memberDao.getMemberBySessionId(sessionMemberId);
                putMemberInCache(sm);
                return sm;
            } catch (EmptyResultDataAccessException ex) {
                throw new MemberNotFoundEx(sessionMemberId);
            }
        }
    }

    /* --- Internal Methods --- */

    private void putMemberInCache(SessionMember member) {
        cache.put(member.getSessionMemberId(), member);
    }
}
