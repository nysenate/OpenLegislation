package gov.nysenate.openleg.service.bill.data;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.service.base.data.CachingService;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.service.bill.event.BillUpdateEvent;
import net.sf.ehcache.*;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data service layer for retrieving and updating bill data. This implementation makes use of
 * in-memory caches to reduce the number of database queries involved in retrieving bill data.
 */
@Service
public class CachedBillDataService implements BillDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataService.class);

    /** Bill cache will store partial bills for performance. */
    @Autowired private CacheManager cacheManager;

    private static final String billCacheName = "bills";
    private Cache billCache;

    /** The maximum heap size (in MB) the bill cache can consume. */
    @Value("${cache.bill.heap.size}")
    private long billCacheSizeMb;

    /** Backing store for bill data. */
    @Autowired private BillDao billDao;

    /** Used to subscribe and post events. */
    @Autowired private EventBus eventBus;

    @PostConstruct
    private void init() {
        setupCaches();
        eventBus.register(this);
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(billCacheName);
    }

    /** --- CachingService implementation --- */

    /** {@inheritDoc} */
    @Override
    public Ehcache getCache() {
        return billCache;
    }

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        this.billCache = new Cache(new CacheConfiguration().name(billCacheName)
            .eternal(true)
            .maxBytesLocalHeap(billCacheSizeMb, MemoryUnit.MEGABYTES)
            .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(this.billCache);
        this.billCache.setMemoryStoreEvictionPolicy(new BillCacheEvictionPolicy());
    }

    /** {@inheritDoc} */
    @Override
    public void evictCaches() {
        logger.info("Clearing the bill cache.");
        this.billCache.removeAll();
    }

    /**
     * Pre-load the bill cache by first clearing its current contents and then requesting every bill in the
     * current session year.
     */
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up bill cache.");
        getBillIds(SessionYear.current(), LimitOffset.ALL).forEach(id -> getBill(id));
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public synchronized void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.BILL)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    public synchronized void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.BILL)) {
            warmCaches();
        }
    }

    /** --- BillDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    public Bill getBill(BaseBillId billId) throws BillNotFoundEx {
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null");
        }
        try {
            Bill bill;
            if (billCache.get(billId) != null) {
                bill = constructBillFromCache(billId);
                logger.debug("Cache hit for bill {}", bill);
            }
            else {
                logger.debug("Fetching bill {}..", billId);
                bill = billDao.getBill(billId);
                putStrippedBillInCache(bill);
            }
            return bill;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new BillNotFoundEx(billId, ex);
        }
        catch (CloneNotSupportedException e) {
            throw new CacheException("Failed to cache retrieved Bill: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public BillInfo getBillInfo(BaseBillId billId) throws BillNotFoundEx {
        logger.debug("Fetching bill info {}..", billId);
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null");
        }
        if (billCache.get(billId) != null) {
            return new BillInfo((Bill) billCache.get(billId).getObjectValue());
        }
        try {
            return billDao.getBillInfo(billId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new BillNotFoundEx(billId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limitOffset) {
        if (sessionYear == null) {
            throw new IllegalArgumentException("SessionYear cannot be null");
        }
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        return billDao.getBillIds(sessionYear, limitOffset, SortOrder.ASC);
    }

    /** {@inheritDoc} */
    @Override
    public int getBillCount(SessionYear sessionYear) {
        if (sessionYear == null) {
            throw new IllegalArgumentException("SessionYear cannot be null");
        }
        return billDao.getBillCount(sessionYear);
    }

    /** {@inheritDoc} */
    @Override
    public void saveBill(Bill bill, SobiFragment fragment, boolean postUpdateEvent) {
        logger.debug("Persisting bill {}", bill);
        billDao.updateBill(bill, fragment);
        putStrippedBillInCache(bill);
        if (postUpdateEvent) {
            eventBus.post(new BillUpdateEvent(bill, LocalDateTime.now()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Range<SessionYear>> activeSessionRange() {
        try {
            return Optional.of(billDao.activeSessionRange());
        }
        catch (DataAccessException ex) {
            return Optional.empty();
        }
    }

    /** --- Internal Methods --- */

    /**
     * Retrieves the bill from the cache. You must check that the bill exists prior to calling this
     * method. The fulltext and memo are put back into a copy of the cached bill.
     *
     * @param billId BaseBillId
     * @return Bill
     * @throws CloneNotSupportedException
     */
    private Bill constructBillFromCache(BaseBillId billId) throws CloneNotSupportedException {
        Bill cachedBill = (Bill) billCache.get(billId).getObjectValue();
        cachedBill = cachedBill.shallowClone();
        billDao.applyText(cachedBill);
        return cachedBill;
    }

    /**
     * In order to cache bills effectively, we strip out the memos and full text from the bill first
     * to save some heap space.
     * @param bill Bill
     */
    private void putStrippedBillInCache(final Bill bill) {
        if (bill != null) {
            try {
                Bill cacheBill = bill.shallowClone();
                cacheBill.getAmendmentList().stream().forEach(ba -> {
                    ba.setMemo("");
                    ba.setFullText("");
                });
                this.billCache.put(new Element(cacheBill.getBaseBillId(), cacheBill));
            }
            catch (CloneNotSupportedException e) {
                logger.error("Failed to cache bill!", e);
            }
        }
    }
}