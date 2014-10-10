package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
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

    /** The name of the bill data cache */
    private static final String billDataCache = "billData";

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
        cacheManager.addCache(billDataCache);
    }

    @Override
    @CacheEvict(value = billDataCache, allEntries = true)
    public void evictCaches() {}

    /** --- BillDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    /** FIXME: Cache key should use the base bill id. */
    public Bill getBill(BillId billId) throws BillNotFoundEx {
        logger.debug("Fetching bill {}..", billId);
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null");
        }
        try {
            return billDao.getBill(billId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new BillNotFoundEx(billId, ex);
        }
    }

    @Override
    public BillInfo getBillInfo(BillId billId) throws BillNotFoundEx {
        logger.debug("Fetching bill info {}..", billId);
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null");
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
        return billDao.getBillIds(sessionYear, limitOffset);
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
    }
}