package gov.nysenate.openleg.search.committee;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class CommitteeServiceTest extends BaseTests{
    private static Logger logger = LoggerFactory.getLogger(CommitteeServiceTest.class);

    @Autowired
    CommitteeDataService committeeDataService;

    @Test
    public void getCommitteeTest() throws Exception {
        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, "Aging");
        Committee committee;
        committee = committeeDataService.getCommittee(committeeId);
        committee = committeeDataService.getCommittee(committeeId);
    }
}
