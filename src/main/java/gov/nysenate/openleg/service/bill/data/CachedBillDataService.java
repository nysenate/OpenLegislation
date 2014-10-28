package gov.nysenate.openleg.service.bill.data;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import gov.nysenate.openleg.service.bill.event.BillUpdateEvent;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.List;

import static net.sf.ehcache.config.SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT;

@Service
public class CachedBillDataService implements BillDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataService.class);

    /** Bill cache will store partial bills for performance. */
    @Autowired private CacheManager cacheManager;
    private Cache billCache;

    /** The maximum heap size the bill cache can consume. */
    @Value("${cache.bill.heap.size}") private long billCacheSizeMb;

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
        cacheManager.removeCache("bills");
    }

    /** --- CachingService implementation --- */

    @Override
    public void setupCaches() {
        this.billCache = new Cache(new CacheConfiguration().name("bills")
            .eternal(true)
            .maxBytesLocalHeap(billCacheSizeMb, MemoryUnit.MEGABYTES)
            .sizeOfPolicy(new SizeOfPolicyConfiguration().maxDepth(50000).maxDepthExceededBehavior(ABORT)));
        cacheManager.addCache(this.billCache);
        this.billCache.setMemoryStoreEvictionPolicy(new BillCacheEvictionPolicy());
    }

    @Override
    public void evictCaches() {
        this.billCache.removeAll();
    }

    /** --- BillDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    public Bill getBill(BaseBillId billId) throws BillNotFoundEx {
        logger.debug("Fetching bill {}..", billId);
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null");
        }
        try {
            Bill bill;
            if (billCache.isKeyInCache(billId)) {
                bill = getBillFromCache(billId);
                logger.debug("Cache hit for bill {}", bill);
            }
            else {
                bill = billDao.getBill(billId);
                putBillInCache(bill);
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
        if (billCache.isKeyInCache(billId)) {
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
        putBillInCache(bill);
        if (postUpdateEvent) {
            eventBus.post(new BillUpdateEvent(bill, LocalDateTime.now()));
        }
    }

    /**
     * Retrieves the bill from the cache. You must check that the bill exists prior to calling this
     * method. The fulltext and memo are put back into a copy of the cached bill.
     *
     * @param billId BaseBillId
     * @return Bill
     * @throws CloneNotSupportedException
     */
    private Bill getBillFromCache(BaseBillId billId) throws CloneNotSupportedException {
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
    private void putBillInCache(final Bill bill) {
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