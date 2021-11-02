package gov.nysenate.openleg.legislation.bill.dao.service;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import org.ehcache.config.EvictionAdvisor;

public class BillCacheEvictionPolicy implements EvictionAdvisor<BaseBillId, Bill> {

    @Override
    public boolean adviseAgainstEviction(BaseBillId key, Bill value) {
        return key.getSession().equals(SessionYear.current()) && value.isPublished();
    }
}
