package gov.nysenate.openleg.service.entity.member.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.entity.member.data.MemberDao;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.service.base.data.CachingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SessionMemberIdCache implements CachingService<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(SessionMemberIdCache.class);

    private EventBus eventBus;
    private Cache memberCache;
    private CacheManager cacheManager;
    private MemberDao memberDao;
    private long memberCacheSizeMb;

    @Autowired
    public SessionMemberIdCache(EventBus eventBus, MemberDao memberDao, CacheManager cacheManager,
                                @Value("${member.cache.heap.size}") long memberCacheSizeMb) {
        this.eventBus = eventBus;
        this.memberDao = memberDao;
        this.cacheManager = cacheManager;
        this.memberCacheSizeMb = memberCacheSizeMb;
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.SESSION_MEMBER.name());
    }

    @Override
    public void setupCaches() {
        this.memberCache = new Cache(new CacheConfiguration()
                .name(ContentCache.SESSION_MEMBER.name())
                .eternal(true)
                .maxBytesLocalHeap(memberCacheSizeMb, MemoryUnit.MEGABYTES)
                .sizeOfPolicy(byteSizeOfPolicy()));
        cacheManager.addCache(this.memberCache);
    }

    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(memberCache);
    }

    @Override
    public void evictContent(Integer sessionMemberId) {
        memberCache.remove(sessionMemberId);
    }

    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.SESSION_MEMBER)) {
            evictCaches();
        }
    }

    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<Integer> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.SESSION_MEMBER)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up Session member cache");
        memberDao.getAllMembers(SortOrder.ASC, LimitOffset.ALL).stream().forEach(this::putMemberInCache);
        logger.info("Done warming up Session member cache");
    }

    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.SESSION_MEMBER)) {
            warmCaches();
        }
    }

    /**
     * Get a SessionMember by sessionMemberId
     *
     * First checks the cache for the session member, if not there they are retrieved from the database and added to the cache.
     * @param sessionMemberId
     * @return
     * @throws MemberNotFoundEx
     */
    public SessionMember getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        Optional<Element> smElement = Optional.ofNullable(memberCache.get(new SimpleKey(sessionMemberId)));
        if (smElement.isPresent()) {
            return (SessionMember) smElement.get().getObjectValue();
        } else {
            try {
                SessionMember sm = memberDao.getMemberBySessionId(sessionMemberId);
                putMemberInCache(sm);
                return sm;
            } catch (EmptyResultDataAccessException ex) {
                throw new MemberNotFoundEx(sessionMemberId);
            }
        }
    }

    /* --- Internal Methods --- */

    private void putMemberInCache(SessionMember member) {
        memberCache.put(new Element(new SimpleKey(member.getSessionMemberId()), member, true));
    }
}
