package gov.nysenate.openleg.service.entity.member.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.entity.member.data.MemberDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.service.base.data.CachingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

@Component
public class SessionChamberShortNameCache implements CachingService<String> {

    private static final Logger logger = LoggerFactory.getLogger(FullMemberIdCache.class);

    private EventBus eventBus;

    private Cache memberCache;

    private CacheManager cacheManager;

    private MemberDao memberDao;

    @Autowired
    public SessionChamberShortNameCache(EventBus eventBus, MemberDao memberDao, CacheManager cacheManager) {
        this.eventBus = eventBus;
        this.memberDao = memberDao;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.SESSION_CHAMBER_SHORTNAME.name());
    }

    @Override
    public void setupCaches() {
        this.memberCache = new Cache(new CacheConfiguration().name(ContentCache.SESSION_CHAMBER_SHORTNAME.name()).eternal(true));
        cacheManager.addCache(this.memberCache);
    }

    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(memberCache);
    }

    @Override
    public void evictContent(String sessionMemberId) {
        memberCache.remove(sessionMemberId);
    }

    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.SESSION_CHAMBER_SHORTNAME)) {
            evictCaches();
        }
    }

    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<String> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.SESSION_CHAMBER_SHORTNAME)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up Session Chamber ShortName cache");
        memberDao.getAllMembers(SortOrder.ASC, LimitOffset.ALL)
                .forEach(this::putMemberInCache);
        logger.info("Done warming up Session Chamber ShortName cache");
    }

    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.SESSION_CHAMBER_SHORTNAME)) {
            warmCaches();
        }
    }

    //CachedMemberService Methods

    public SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null) {
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        }
        String key = genCacheKey(lbdcShortName, sessionYear, chamber);
        if (memberCache.isKeyInCache(key)) {
            return (SessionMember) memberCache.get(key).getObjectValue();
        }
        try {
            SessionMember sm = memberDao.getMemberByShortName(lbdcShortName, sessionYear, chamber);
            putMemberInCache(sm);
            return sm;
        } catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(lbdcShortName, sessionYear, chamber);
        }
    }

    /* --- Internal Methods --- */

    private String genCacheKey(SessionMember sessionMember) {
        return genCacheKey(
                sessionMember.getLbdcShortName(),
                sessionMember.getSessionYear(),
                sessionMember.getChamber()
        );
    }

    private String genCacheKey(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {
        return sessionYear.toString() + "-" + chamber + "-" + lbdcShortName;
    }

    private void putMemberInCache(SessionMember member) {
        String key = genCacheKey(member);
        memberCache.put(new Element(new SimpleKey(key), member, true));
    }
}
