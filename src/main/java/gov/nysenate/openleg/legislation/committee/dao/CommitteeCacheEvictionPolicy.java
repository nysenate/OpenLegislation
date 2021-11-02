package gov.nysenate.openleg.legislation.committee.dao;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import org.ehcache.config.EvictionAdvisor;

import java.util.List;

public class CommitteeCacheEvictionPolicy implements EvictionAdvisor<CommitteeSessionId, List<Committee>> {

    @Override
    public boolean adviseAgainstEviction(CommitteeSessionId key, List<Committee> value) {
        return key.getSession().equals(SessionYear.current()) && value.stream().anyMatch(Committee::isCurrent);
    }
}
