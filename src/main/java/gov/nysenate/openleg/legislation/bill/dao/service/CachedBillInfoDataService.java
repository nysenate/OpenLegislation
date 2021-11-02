package gov.nysenate.openleg.legislation.bill.dao.service;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import org.ehcache.Cache;

public class CachedBillInfoDataService extends CachingService<BaseBillId, BillInfo> {
    @Override
    protected CacheType cacheType() {
        return CacheType.BILL_INFO;
    }

    @Override
    protected Class<BaseBillId> keyClass() {
        return BaseBillId.class;
    }

    @Override
    protected Class<BillInfo> valueClass() {
        return BillInfo.class;
    }

    @Override
    public void warmCaches() {}

    public Cache<BaseBillId, BillInfo> getBillInfoCache() {
        return cache;
    }
}
