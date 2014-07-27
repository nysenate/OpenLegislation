package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.dao.bill.BillDao;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CachedBillDataService implements BillDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataService.class);

    private static final String billDataCache = "billData";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private BillDao billDao;

    @PostConstruct
    private void init() {
        setupCaches();
    }

    @Override
    public void setupCaches() {
        cacheManager.addCache("billData");
    }

    @Override
    @CacheEvict(value = billDataCache, allEntries = true)
    public void evictCaches() {}

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = billDataCache)
    public Bill getBill(BillId billId) throws BillNotFoundEx {
        if (billId == null) {
            throw new IllegalArgumentException("BillId cannot be null!");
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
    @CacheEvict(value = billDataCache, key = "#bill.getBillId()")
    public void saveBill(Bill bill, SobiFragment fragment) {
        logger.debug("Persisting bill {}", bill);
        billDao.updateBill(bill, fragment);
    }
}