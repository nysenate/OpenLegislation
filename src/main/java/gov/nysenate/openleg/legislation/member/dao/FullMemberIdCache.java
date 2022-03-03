package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FullMemberIdCache extends CachingService<Integer, FullMember> {
    private static final Logger logger = LoggerFactory.getLogger(FullMemberIdCache.class);
    private final MemberDao memberDao;

    // TODO: should this be the norm? See other autowired warnings.
    @Autowired
    public FullMemberIdCache(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.FULL_MEMBER;
    }

    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up member cache");
        getAllFullMembers().forEach(this::putMemberInCache);
        logger.info("Done warming up member cache");
    }

    /**
     * {@inheritDoc}
     */
    public List<SessionMember> getAllMembers(SortOrder sortOrder, LimitOffset limOff) {
        return memberDao.getAllMembers(sortOrder, limOff);
    }

    /**
     * {@inheritDoc}
     */
    public List<FullMember> getAllFullMembers() {
        return getAllMembers(SortOrder.ASC, LimitOffset.ALL).stream()
                .collect(Collectors.groupingBy(sm -> sm.getMember().getMemberId(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(FullMember::new)
                .collect(Collectors.toList());
    }

    /**
     * Get a FullMember.
     *
     * Checks the cache first, if not there the member is loaded from the database and saved to the cache.
     * @param memberId
     * @return
     * @throws MemberNotFoundEx
     */
    public FullMember getMemberById(int memberId) throws MemberNotFoundEx {
        FullMember fm = cache.get(memberId);
        if (fm != null) {
            return fm;
        } else {
            fm = memberDao.getMemberById(memberId);
            putMemberInCache(fm);
            return fm;
        }
    }

    /* --- Internal Methods --- */

    private void putMemberInCache(FullMember fm) {
        cache.put(fm.getMemberId(), fm);
    }
}
