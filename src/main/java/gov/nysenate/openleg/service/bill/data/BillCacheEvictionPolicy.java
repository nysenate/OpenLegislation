package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.AbstractPolicy;

public class BillCacheEvictionPolicy extends AbstractPolicy
{
    public static final String NAME = "RECENT_BILL_FIRST";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Determine if element2 is a better candidate for eviction. If element1 contains a bill for the current session
     * (or greater) then we want to evict element2 if it has an earlier session year. Otherwise we can just use
     * the Least Recently Used policy.
     *
     * @param element1 Element
     * @param element2 Element
     * @return boolean
     */
    @Override
    public boolean compare(Element element1, Element element2) {
        int element1Year = ((BaseBillId) element1.getObjectKey()).getSession().getYear();
        int element2Year = ((BaseBillId) element2.getObjectKey()).getSession().getYear();
        return (element1Year >= SessionYear.current().getYear() && element1Year != element2Year)
            ? (element2Year < element1Year)
            : (element2.getLastAccessTime() < element1.getLastAccessTime());
    }
}
