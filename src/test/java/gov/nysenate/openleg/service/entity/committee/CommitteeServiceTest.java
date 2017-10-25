package gov.nysenate.openleg.service.entity.committee;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
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
