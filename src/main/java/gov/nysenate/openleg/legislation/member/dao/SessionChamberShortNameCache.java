package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.ContentCache;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
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
import java.util.Locale;

@Component
public class SessionChamberShortNameCache extends CachingService<String, SessionMember> {

    private static final Logger logger = LoggerFactory.getLogger(FullMemberIdCache.class);

    private final MemberDao memberDao;
    @Value("${shortname.cache.heap.size}")
    private int shortnameCacheSizeMb;

    @Autowired
    public SessionChamberShortNameCache(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    protected List<ContentCache> getCacheEnums() {
        return List.of(ContentCache.SESSION_CHAMBER_SHORTNAME);
    }

    @Override
    protected boolean isByteSizeOf() {
        return true;
    }

    @Override
    protected int getNumUnits() {
        return shortnameCacheSizeMb;
    }

    @Override
    protected ResourceUnit getUnit() {
        return MemoryUnit.MB;
    }

    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up Session Chamber ShortName cache");
        memberDao.getAllMembers(SortOrder.ASC, LimitOffset.ALL)
                .forEach(this::putMemberInCache);
        logger.info("Done warming up Session Chamber ShortName cache");
    }


    /**
     * Gets a member from our cache or else loads it from the database and adds them to the cache.
     * @return
     * @throws MemberNotFoundEx
     */
    public SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null)
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        SessionMember sm = cache.get(genCacheKey(lbdcShortName, sessionYear, chamber));
        if (sm != null)
            return sm;
        else {
            try {
                sm = memberDao.getMemberByShortName(lbdcShortName, sessionYear, chamber);
                putMemberInCache(sm);
                return sm;
            } catch (EmptyResultDataAccessException ex) {
                throw new MemberNotFoundEx(lbdcShortName, sessionYear, chamber);
            }
        }
    }

    /* --- Internal Methods --- */

    private String genCacheKey(SessionMember sessionMember) {
        return genCacheKey(
                sessionMember.getLbdcShortName(),
                sessionMember.getSessionYear(),
                sessionMember.getMember().getChamber()
        );
    }

    /**
     * Generate a unique key used to identify an individual session member.
     */
    private String genCacheKey(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {
        return sessionYear.toString() + "-" + chamber.name() + "-" + lbdcShortName.toUpperCase(Locale.US);
    }

    private void putMemberInCache(SessionMember member) {
        cache.put(genCacheKey(member), member);
    }
}
