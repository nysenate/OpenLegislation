package gov.nysenate.openleg.legislation.bill.dao.service;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import org.ehcache.Cache;
import org.springframework.stereotype.Service;

@Service
public class CachedBillInfoDataService extends CachingService<BaseBillId, BillInfo> {
    @Override
    protected CacheType cacheType() {
        return CacheType.BILL_INFO;
    }

    @Override
    public void warmCaches() {}

    public Cache<BaseBillId, BillInfo> getBillInfoCache() {
        return cache;
    }
}
