package gov.nysenate.openleg.service.bill.data;

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

    @PostConstruct
    private void init() {
        setupCaches();
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
                // Cached bills need to have their fulltext and memos filled in.
                bill = (Bill) billCache.get(billId).getObjectValue();
                bill = bill.shallowClone();
                billDao.applyText(bill);
            }
            else {
                bill = billDao.getBill(billId);
                // When caching the retrieved bill, we use a shallow copy with no fulltext or memos.
                // This is to optimize the number of bills we can cache into memory.
                Bill cacheBill = bill.shallowClone();
                cacheBill.getAmendmentList().stream().forEach(ba -> {
                    ba.setMemo("");
                    ba.setFullText("");
                });
                this.billCache.put(new Element(billId, cacheBill));
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
        billCache.put(new Element(bill.getBaseBillId(), bill));
    }
}