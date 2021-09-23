package gov.nysenate.openleg.search.committee;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class CommitteeSearchServiceTest extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(CommitteeSearchServiceTest.class);

    @Autowired
    CommitteeSearchService committeeSearchService;

    @Test
    public void purgeIndexTest() {
        committeeSearchService.clearIndex();
    }

    @Test
    public void indexAllTest() {
        committeeSearchService.rebuildIndex();
    }
}
