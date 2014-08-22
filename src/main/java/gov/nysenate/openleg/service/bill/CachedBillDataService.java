package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillNotFoundEx;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Cacheable(value = billDataCache)
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
    @CacheEvict(value = billDataCache, key = "#bill.getBaseBillId()")
    public void saveBill(Bill bill, SobiFragment fragment) {
        logger.debug("Persisting bill {}", bill);
        billDao.updateBill(bill, fragment);
    }
}