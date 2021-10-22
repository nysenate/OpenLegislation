package gov.nysenate.openleg.legislation.bill.dao.service;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.ContentCache;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.bill.dao.BillDao;
import gov.nysenate.openleg.legislation.bill.exception.BillNotFoundEx;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.updates.bill.BillUpdateEvent;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data service layer for retrieving and updating bill data. This implementation makes use of
 * in-memory caches to reduce the number of database queries involved in retrieving bill data.
 */
@Service
public class CachedBillDataService extends CachingService<BaseBillId> implements BillDataService {
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataService.class);

    @Autowired
    private BillDao billDao;

    @Value("${bill.cache.element.size}") private int billCacheElementSize;
    @Value("${bill-info.cache.element.size}") private int billInfoCacheElementSize;

    private Cache billCache;
    private Cache billInfoCache;

    public CachedBillDataService() {
        super(cache);
    }

    @PreDestroy
    protected void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.BILL.name());
        cacheManager.removeCache(ContentCache.BILL_INFO.name());
    }

    /** --- CachingService implementation --- */

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        // Partial bill cache will store Bill instances with the full text fields stripped to save space.
        this.billCache = new Cache(new CacheConfiguration().name(ContentCache.BILL.name())
            .eternal(true)
            .maxEntriesLocalHeap(billCacheElementSize)
            .sizeOfPolicy(elementSizeOfPolicy()));
        cacheManager.addCache(this.billCache);
        // This can only be called after the cache is added to the cache manager.
        this.billCache.setMemoryStoreEvictionPolicy(new BillCacheEvictionPolicy());

        // Bill Info cache will store BillInfo instances to speed up search and listings.
        // If a bill is already stored in the billCache, it's BillInfo does not need to be stored here.
        this.billInfoCache = new Cache(new CacheConfiguration().name(ContentCache.BILL_INFO.name())
            .eternal(true)
            .maxEntriesLocalHeap(billInfoCacheElementSize)
            .sizeOfPolicy(elementSizeOfPolicy()));
        cacheManager.addCache(this.billInfoCache);
    }

    /**
     * Pre-load the bill caches by clearing out each of their contents and then loading:
     * Bill Cache - Current session year bills only
     * Bill Info Cache - Bill Infos from all available session years.
     */
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up bill cache.");
        Optional<Range<SessionYear>> sessionRange = activeSessionRange();
        if (sessionRange.isPresent()) {
            SessionYear sessionYear = sessionRange.get().lowerEndpoint();
            while (sessionYear.compareTo(sessionRange.get().upperEndpoint()) <= 0) {
                if (sessionYear.equals(SessionYear.current())) {
                    logger.info("Caching Bill instances for current session year: {}", sessionYear);
                    // Don't load any text because that is not cached.
                    getBillIds(sessionYear, LimitOffset.ALL).forEach(id -> getBill(id));
                }
                else {
                    logger.info("Caching Bill Info instances for session year: {}", sessionYear);
                    getBillIds(sessionYear, LimitOffset.ALL).forEach(this::getBillInfo);
                }
                sessionYear = sessionYear.next();
            }
        }
        logger.info("Done warming up bill cache.");
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(BaseBillId baseBillId) {
        logger.debug("evicting {}", baseBillId);
        billInfoCache.remove(baseBillId);
        billCache.remove(baseBillId);
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
        if (billInfoCache.get(billId) != null) {
            return (BillInfo) billInfoCache.get(billId).getObjectValue();
        }
        try {
            BillInfo billInfo = billDao.getBillInfo(billId);
            billInfoCache.put(new Element(billId, billInfo));
            return billInfo;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new BillNotFoundEx(billId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public BillInfo getBillInfoSafe(BaseBillId billId) {
        try {
            return getBillInfo(billId);
        } catch (BillNotFoundEx ex) {
            BillInfo dummyInfo = new BillInfo();
            dummyInfo.setBillId(billId);
            String message = "Data is currently not available for this bill";
            dummyInfo.setTitle(message);
            dummyInfo.setSummary(message);
            return dummyInfo;
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
    public synchronized int getBillCount(SessionYear sessionYear) {
        if (sessionYear == null) {
            throw new IllegalArgumentException("SessionYear cannot be null");
        }
        return billDao.getBillCount(sessionYear);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void saveBill(Bill bill, LegDataFragment fragment, boolean postUpdateEvent) {
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

    /** {@inheritDoc} */
    @Override
    public Optional<String> getAlternateBillPdfUrl(BillId billId) {
        try {
            return Optional.of(billDao.getAlternateBillPdfUrl(billId));
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
        billDao.applyTextAndMemo(cachedBill);
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
                cacheBill.getAmendmentList().forEach(ba -> {
                    ba.setMemo("");
                    ba.clearFullTexts();
                });
                this.billCache.put(new Element(cacheBill.getBaseBillId(), cacheBill));
                // Remove entry from the bill info cache if it exists
                this.billInfoCache.remove(cacheBill.getBaseBillId());
            }
            catch (CloneNotSupportedException e) {
                logger.error("Failed to cache bill!", e);
            }
        }
    }
}