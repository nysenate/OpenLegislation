package gov.nysenate.openleg.service.law.search;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.service.law.event.LawTreeUpdateEvent;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class ElasticLawSearchServiceTest extends BaseTests {

    @Autowired ElasticLawSearchService lawSearchService;

    @Test
    public void handleLawTreeUpdateTest() {
        lawSearchService.handleLawTreeUpdate(new LawTreeUpdateEvent("ABC"));
    }
}