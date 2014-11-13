package gov.nysenate.openleg.service.entity.committee.data;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.AbstractPolicy;

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
        int element1Year = ((CommitteeSessionId) element1.getObjectKey()).getSession().getYear();
        int element2Year = ((CommitteeSessionId) element2.getObjectKey()).getSession().getYear();
        return (element1Year >= SessionYear.current().getYear() && element1Year != element2Year)
                ? (element2Year < element1Year)
                : (element2.getLastAccessTime() < element1.getLastAccessTime());
    }
}
