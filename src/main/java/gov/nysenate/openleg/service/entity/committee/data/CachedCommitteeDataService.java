package gov.nysenate.openleg.service.entity.committee.data;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.entity.committee.data.CommitteeDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.data.CachingService;
import gov.nysenate.openleg.service.entity.committee.event.CommitteeUpdateEvent;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CachedCommitteeDataService implements CommitteeDataService, CachingService<CommitteeSessionId> {

    private static final Logger logger = LoggerFactory.getLogger(CachedCommitteeDataService.class);

    @Autowired private CacheManager cacheManager;
    @Autowired private CommitteeDao committeeDao;
    @Autowired private EventBus eventBus;

    @Value("${committee.cache.size}") private long committeeCacheSizeMb;

    private Cache committeeCache;

    @PostConstruct
    private void init() {
        setupCaches();
        eventBus.register(this);
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.COMMITTEE.name());
    }

    /** --- Cache Management --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(committeeCache);
    }

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        committeeCache = new Cache(new CacheConfiguration().name(ContentCache.COMMITTEE.name())
                .eternal(true)
                .maxBytesLocalHeap(committeeCacheSizeMb, MemoryUnit.MEGABYTES)
                .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(committeeCache);
        committeeCache.setMemoryStoreEvictionPolicy(new CommitteeCacheEvictionPolicy());
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(CommitteeSessionId committeeSessionId) {
        committeeCache.remove(committeeSessionId);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public synchronized void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.COMMITTEE)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<CommitteeSessionId> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.COMMITTEE)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up committee cache.");
        getCommitteeList(Chamber.SENATE, LimitOffset.ALL);
        getCommitteeList(Chamber.ASSEMBLY, LimitOffset.ALL);
    }

    /** {@inheritDoc} */
    @Override
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.COMMITTEE)) {
            warmCaches();
        }
    }

    /** --- Committee Data Services --- */

    /**
     * {@inheritDoc}
     * @param committeeSessionId
     */
    @Override
    public Committee getCommittee(CommitteeSessionId committeeSessionId) throws CommitteeNotFoundEx {
        if (committeeSessionId == null) {
            throw new IllegalArgumentException("committeeSessionId cannot be null!");
        }
        try {
            return getCommitteeHistory(committeeSessionId).get(0);
        } catch (IndexOutOfBoundsException ex) {
            throw new CommitteeNotFoundEx(committeeSessionId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws CommitteeNotFoundEx {
        if (committeeVersionId == null) {
            throw new IllegalArgumentException("committeeVersionId cannot be null!");
        }
        LocalDateTime refDate = committeeVersionId.getReferenceDate();
        for (Committee committee : getCommitteeHistory(committeeVersionId)) {
            if ( committee.getCreated().equals(refDate) ||
                     committee.getCreated().isBefore(refDate)  &&
                    (committee.getReformed()==null || committee.getReformed().isAfter(refDate)) ) {
                return committee;
            }
        }
        throw new CommitteeNotFoundEx(committeeVersionId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommitteeId> getCommitteeIds() {
        return committeeDao.getCommitteeList();
    }

    @Override
    public List<SessionYear> getEligibleYears() {
        return committeeDao.getEligibleYears();
    }

    @Override
    public List<CommitteeSessionId> getAllCommitteeSessionIds() {
        return committeeDao.getAllSessionIds();
    }

    /** {@inheritDoc} */
    @Override
    public List<Committee> getCommitteeList(Chamber chamber, SessionYear sessionYear, LimitOffset limitOffset) {
        if (chamber == null) {
            throw new IllegalArgumentException("Chamber cannot be null!");
        }

        List<Committee> committeeList = new ArrayList<>();
        getCommitteeIds().stream()
                .filter(committeeId -> committeeId.getChamber().equals(chamber))
                .map(committeeId -> new CommitteeSessionId(committeeId, sessionYear))
                .forEach(committeeSessionId -> {
                    try {
                        committeeList.add(getCommittee(committeeSessionId));
                    } catch (CommitteeNotFoundEx ignored) {}
                });

        return LimitOffset.limitList(committeeList, limitOffset != null ? limitOffset : LimitOffset.ALL);
    }

    /** {@inheritDoc} */
    @Override
    public int getCommitteeListCount(Chamber chamber, SessionYear sessionYear) {
        return getCommitteeList(chamber, sessionYear, LimitOffset.ALL).size();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId,
                                               LimitOffset limitOffset, SortOrder order) throws CommitteeNotFoundEx {
        if (committeeSessionId ==null) {
            throw new IllegalArgumentException("CommitteeSessionId cannot be null!");
        }

        List<Committee> committeeHistory;

        Element element = committeeCache.get(committeeSessionId);
        if (element != null) {
            logger.debug("Committee cache hit for {}", committeeSessionId);
            committeeHistory = (List<Committee>) element.getObjectValue();
        }
        else {
            try {
                committeeHistory = committeeDao.getCommitteeHistory(committeeSessionId);
                committeeCache.put(new Element(committeeSessionId, committeeHistory));
                logger.debug("Added committee history {} to cache", committeeSessionId);
            }
            catch (EmptyResultDataAccessException ex){
                throw new CommitteeNotFoundEx(committeeSessionId, ex);
            }
        }

        // The dao provides the result already in DESC order by created date
        if (order != null && order.equals(SortOrder.ASC)) {
            committeeHistory = Lists.reverse(committeeHistory);
        }
        if (limitOffset != null && !limitOffset.equals(LimitOffset.ALL)) {
            committeeHistory = LimitOffset.limitList(committeeHistory, limitOffset);
        }
        return committeeHistory;
    }

    /** {@inheritDoc}
     * @param committeeSessionId*/
    @Override
    public int getCommitteeHistoryCount(CommitteeSessionId committeeSessionId) {
        try {
            return getCommitteeHistory(committeeSessionId).size();
        } catch (CommitteeNotFoundEx ex) {
            return 0;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveCommittee(Committee committee, SobiFragment sobiFragment) {
        if(committee==null) {
            throw new IllegalArgumentException("Committee cannot be null.");
        }
        committeeDao.updateCommittee(committee, sobiFragment);
        committeeCache.remove(committee.getSessionId());
        eventBus.post(new CommitteeUpdateEvent(committee, LocalDateTime.now()));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteCommittee(CommitteeId committeeId) {
        if(committeeId==null) {
            throw new IllegalArgumentException("CommitteeId cannot be null!");
        }
        committeeDao.deleteCommittee(committeeId);
    }
}
