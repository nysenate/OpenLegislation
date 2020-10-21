package gov.nysenate.openleg.search.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.updates.law.LawTreeUpdateEvent;
import gov.nysenate.openleg.search.law.ElasticLawSearchService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class ElasticLawSearchServiceTest extends BaseTests {

    @Autowired
    ElasticLawSearchService lawSearchService;

    @Test
    public void handleLawTreeUpdateTest() {
        lawSearchService.handleLawTreeUpdate(new LawTreeUpdateEvent("ABC"));
    }
}