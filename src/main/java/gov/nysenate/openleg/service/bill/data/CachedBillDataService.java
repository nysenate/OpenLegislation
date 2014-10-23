package gov.nysenate.openleg.service.bill.data;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CachedBillDataService implements BillDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataService.class);

    private Cache billCache;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private BillDao billDao;

    @Autowired
    private EventBus eventBus;

    @PostConstruct
    private void init() {
        setupCaches();
        eventBus.register(this);
    }

    /** --- CachingService implementation --- */

    @Override
    public void setupCaches() {
        cacheManager.addCache("bills");
        this.billCache = cacheManager.getCache("bills");
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
            }
            else {
                bill = billDao.getBill(billId);
                // When caching the retrieved bill, we use a shallow copy with no fulltext or memos.
                // This is to optimize the number of bills we can cache into memory.
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
    public void saveBill(Bill bill, SobiFragment fragment) {
        logger.debug("Persisting bill {}", bill);
        billDao.updateBill(bill, fragment);
        putBillInCache(bill);
        eventBus.post(new BillUpdateEvent(bill, LocalDateTime.now()));
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