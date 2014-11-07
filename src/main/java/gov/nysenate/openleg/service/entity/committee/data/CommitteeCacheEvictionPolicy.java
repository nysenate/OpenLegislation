package gov.nysenate.openleg.service.entity.committee.data;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.AbstractPolicy;
import org.apache.commons.lang3.StringUtils;

public class CommitteeCacheEvictionPolicy extends AbstractPolicy {

    public static final String name = "RECENT_COMMITTEE_FIRST";

    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean compare(Element element1, Element element2) {
        if (element1.getObjectValue() instanceof String &&
                StringUtils.equals((String) element1.getObjectValue(), CachedCommitteeDataService.committeeIdListKey)) {
            return true;
        }
        else
        if (element2.getObjectValue() instanceof String &&
                StringUtils.equals((String) element2.getObjectValue(), CachedCommitteeDataService.committeeIdListKey)) {
            return false;
        }
        int element1Year = ((CommitteeSessionId) element1.getObjectKey()).getSession().getYear();
        int element2Year = ((CommitteeSessionId) element2.getObjectKey()).getSession().getYear();
        return (element1Year >= SessionYear.current().getYear() && element1Year != element2Year)
                ? (element2Year < element1Year)
                : (element2.getLastAccessTime() < element1.getLastAccessTime());
    }
}
