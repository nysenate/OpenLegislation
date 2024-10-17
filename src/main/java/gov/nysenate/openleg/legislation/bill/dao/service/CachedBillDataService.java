package gov.nysenate.openleg.legislation.bill.dao.service;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
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
import org.ehcache.config.EvictionAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Data service layer for retrieving and updating bill data. This implementation makes use of
 * in-memory caches to reduce the number of database queries involved in retrieving bill data.
 */
@Service
public class CachedBillDataService extends CachingService<BaseBillId, Bill> implements BillDataService {
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataService.class);

    @Autowired
    public CachedBillDataService(BillDao billDao, CachedBillInfoDataService billInfoDataService) {
        this.billDao = billDao;
        this.billInfoDataService = billInfoDataService;
    }

    private final BillDao billDao;
    // Bill Info cache will store BillInfo instances to speed up search and listings.
    // If a bill is already stored in the main cache, its BillInfo does not need to be stored here.
    private final CachedBillInfoDataService billInfoDataService;

    @Service
    static class CachedBillInfoDataService extends CachingService<BaseBillId, BillInfo> {
        @Override
        protected CacheType cacheType() {
            return CacheType.BILL_INFO;
        }

        private Cache<BaseBillId, BillInfo> cache() {
            return cache;
        }
    }

    /** --- CachingService implementation --- */

    @Override
    protected CacheType cacheType() {
        return CacheType.BILL;
    }

    @Override
    protected EvictionAdvisor<BaseBillId, Bill> evictionAdvisor() {
        return (key, value) -> key.getSession().equals(SessionYear.current()) &&
                value.isPublished();
    }

    public void evictBill(BaseBillId baseBillId) {
        cache.remove(baseBillId);
        billInfoDataService.cache().remove(baseBillId);
    }

    /* --- BillDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    public Bill getBill(BaseBillId billId) throws BillNotFoundEx {
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null");
        }
        Bill bill = cache.get(billId);
        if (bill != null) {
            logger.debug("Cache hit for bill {}", bill);
            try {
                bill = bill.shallowClone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException("Failed to cache retrieved Bill: " + e.getMessage());
            }
            billDao.applyTextAndMemo(bill);
        }
        else {
            logger.debug("Fetching bill {}...", billId);
            try {
                bill = billDao.getBill(billId);
            }
            catch (EmptyResultDataAccessException ex) {
                throw new BillNotFoundEx(billId, ex);
            }
            putStrippedBillInCache(bill);
        }
        return bill;
    }

    /** {@inheritDoc} */
    @Override
    public BillInfo getBillInfo(BaseBillId billId) throws BillNotFoundEx {
        logger.debug("Fetching bill info {}...", billId);
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null");
        }
        Bill bill = cache.get(billId);
        if (bill != null) {
            return new BillInfo(bill);
        }
        BillInfo info = billInfoDataService.cache().get(billId);
        if (info != null) {
            return info;
        }
        try {
            info = billDao.getBillInfo(billId);
            billInfoDataService.cache().put(billId, info);
            return info;
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
    public synchronized void saveBill(Bill bill, LegDataFragment fragment, boolean postUpdateEvent) {
        logger.debug("Persisting bill {}", bill);
        billDao.updateBill(bill, fragment);
        putStrippedBillInCache(bill);
        if (postUpdateEvent) {
            eventBus.post(new BillUpdateEvent(bill));
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

    /* --- Internal Methods --- */

    /**
     * In order to cache bills effectively, we strip out the memos and full text from the bill first
     * to save some heap space.
     * @param bill Bill
     */
    private void putStrippedBillInCache(final Bill bill) {
        if (bill == null) {
            return;
        }
        try {
            Bill cacheBill = bill.shallowClone();
            cacheBill.getAmendmentList().forEach(ba -> {
                ba.setMemo("");
                ba.clearFullTexts();
            });
            cache.put(cacheBill.getBaseBillId(), cacheBill);
            // Remove entry from the bill info cache if it exists
            billInfoDataService.cache().remove(cacheBill.getBaseBillId());
        }
        catch (CloneNotSupportedException e) {
            logger.error("Failed to cache bill!", e);
        }
    }
}