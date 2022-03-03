package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class SessionMemberIdCache extends CachingService<Integer, SessionMember> {
    private static final Logger logger = LoggerFactory.getLogger(SessionMemberIdCache.class);
    private final MemberDao memberDao;

    @Autowired
    public SessionMemberIdCache(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.SESSION_MEMBER;
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
