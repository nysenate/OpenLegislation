package gov.nysenate.openleg.legislation.member.dao;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import gov.nysenate.openleg.legislation.CacheEvictEvent;
import gov.nysenate.openleg.legislation.CacheEvictIdEvent;
import gov.nysenate.openleg.legislation.CacheWarmEvent;
import gov.nysenate.openleg.legislation.ContentCache;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.legislation.CachingService;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FullMemberIdCache implements CachingService<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(FullMemberIdCache.class);

    private EventBus eventBus;
    private Cache memberCache;
    private CacheManager cacheManager;
    private MemberDao memberDao;
    private long fullMemberCacheSizeMb;

    @Autowired
    public FullMemberIdCache(EventBus eventBus, MemberDao memberDao, CacheManager cacheManager,
                             @Value("${full_member.cache.heap.size}") long fullMemberCacheSizeMb) {
        this.eventBus = eventBus;
        this.memberDao = memberDao;
        this.cacheManager = cacheManager;
        this.fullMemberCacheSizeMb = fullMemberCacheSizeMb;
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.FULL_MEMBER.name());
    }

    @Override
    public void setupCaches() {
        this.memberCache = new Cache(new CacheConfiguration().name(ContentCache.FULL_MEMBER.name())
                .eternal(true)
                .maxBytesLocalHeap(fullMemberCacheSizeMb, MemoryUnit.MEGABYTES)
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
        if (evictEvent.affects(ContentCache.FULL_MEMBER)) {
            evictCaches();
        }
    }

    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<Integer> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.FULL_MEMBER)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up member cache");
        getAllFullMembers().stream()
                .forEach(this::putMemberInCache);
        logger.info("Done warming up member cache");
    }

    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.FULL_MEMBER)) {
            warmCaches();
        }
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
        Optional<Element> fmElement = Optional.ofNullable(memberCache.get(new SimpleKey(memberId)));
        if (fmElement.isPresent()) {
            return (FullMember) fmElement.get().getObjectValue();
        } else {
            FullMember member = memberDao.getMemberById(memberId);
            putMemberInCache(member);
            return member;
        }
    }

    /* --- Internal Methods --- */

    private void putMemberInCache(FullMember member) {
        memberCache.put(new Element(new SimpleKey(member.getMemberId()), member, true));
    }
}
