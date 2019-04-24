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
import gov.nysenate.openleg.model.entity.FullMember;
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

    @Autowired
    public FullMemberIdCache(EventBus eventBus, MemberDao memberDao, CacheManager cacheManager) {
        this.eventBus = eventBus;
        this.memberDao = memberDao;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
        warmCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.FULL_MEMBER.name());
    }

    public void setupCaches() {
        this.memberCache = new Cache(new CacheConfiguration().name(ContentCache.FULL_MEMBER.name()).eternal(true));
        cacheManager.addCache(this.memberCache);
    }

    public List<Ehcache> getCaches() {
        return Arrays.asList(memberCache);
    }

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

    public boolean isKeyInCache(SimpleKey key) {
        return memberCache.isKeyInCache(key);
    }

    public void putMemberInCache(FullMember member) {
        memberCache.put(new Element(new SimpleKey(member.getMemberId()), member, true));
    }

    public Cache getCache() {
        return memberCache;
    }

    /** {@inheritDoc} */
    public List<SessionMember> getAllMembers(SortOrder sortOrder, LimitOffset limOff) {
        return memberDao.getAllMembers(sortOrder, limOff);
    }

    /** {@inheritDoc} */
    public List<FullMember> getAllFullMembers() {
        return getAllMembers(SortOrder.ASC, LimitOffset.ALL).stream()
                .collect(Collectors.groupingBy(SessionMember::getMemberId, LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(FullMember::new)
                .collect(Collectors.toList());
    }



    //CachedMemberService Methods

    public SessionMember getMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx {
        if (memberCache.isKeyInCache(memberId)) {

            return Optional.ofNullable((FullMember)
                    memberCache.get(memberId).getObjectValue())
                    .flatMap(fullMember -> fullMember.getSessionMemberForYear(sessionYear))
                    .orElse(null);
        }
        return null;
    }

    public FullMember getMemberById(int memberId) throws MemberNotFoundEx {
        if (memberCache.isKeyInCache(memberId)) {

            return Optional.ofNullable((FullMember)
                            memberCache.get(memberId).getObjectValue())
                            .orElse(null);
        }
        return null;
    }
}
