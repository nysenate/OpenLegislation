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
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.base.data.CachingService;
import gov.nysenate.openleg.service.entity.member.event.UnverifiedMemberEvent;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class CachedMemberService implements MemberService, CachingService<Integer>
{
    private static final Logger logger = LoggerFactory.getLogger(CachedMemberService.class);

    @Autowired
    private EventBus eventBus;

    private Cache memberCache;
    private static final String memberCacheName = "members";

    @Autowired
    private CacheManager cacheManager;

    @Resource(name = "sqlMember")
    private MemberDao memberDao;

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
        warmCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(memberCacheName);
    }

    /** --- Caching Service Implementation --- */

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        this.memberCache = new Cache(new CacheConfiguration().name(memberCacheName).eternal(true));
        cacheManager.addCache(this.memberCache);
    }

    /** {@inheritDoc} */
    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(memberCache);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.MEMBER)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<Integer> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.MEMBER)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(Integer sessionMemberId) {
        memberCache.remove(sessionMemberId);
    }

    /** {@inheritDoc} */
    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up member cache");
        memberDao.getAllMembers(SortOrder.ASC, LimitOffset.ALL).stream().forEach(this::putMemberInCache);
        logger.info("Done warming up member cache");
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.MEMBER)) {
            warmCaches();
        }
    }

    /** --- MemberService implementation --- */

    /** {@inheritDoc} */
    @Override
    public Member getMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx {
        if (memberId <= 0) {
            throw new IllegalArgumentException("Member Id cannot be less than or equal to 0.");
        }
        try {
            return memberDao.getMemberById(memberId, sessionYear);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(memberId, sessionYear);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        SimpleKey key = new SimpleKey(sessionMemberId);
        if (memberCache.isKeyInCache(key)) {
            return (Member) memberCache.get(key).getObjectValue();
        }
        try {
            Member member = memberDao.getMemberBySessionId(sessionMemberId);
            putMemberInCache(member);
            return member;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(sessionMemberId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null) {
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        }
        try {
            return memberDao.getMemberByShortName(lbdcShortName, sessionYear, chamber);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(lbdcShortName, sessionYear, chamber);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberByShortNameEnsured(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws ParseError {
        try {
            return getMemberByShortName(lbdcShortName, sessionYear, chamber);
        }
        catch (MemberNotFoundEx ex) {
            Member member = Member.newMakeshiftMember(lbdcShortName, sessionYear, chamber);
            memberDao.insertUnverifiedSessionMember(member);
            eventBus.post(new UnverifiedMemberEvent(member));
            return member;
        }
    }

    @Override
    public List<Member> getAllMembers(SortOrder sortOrder, LimitOffset limOff) {
            return memberDao.getAllMembers(sortOrder, limOff);
    }

    private void putMemberInCache(Member member) {
        memberCache.put(new Element(new SimpleKey(member.getSessionMemberId()), member, true));
    }
}