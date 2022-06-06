package gov.nysenate.openleg.legislation.law.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.api.CacheStatsView;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.CacheType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(IntegrationTest.class)
public class CachedLawDataServiceIT extends BaseTests {
    @Autowired
    private CachedLawDataService dataService;

    @Test
    public void basicTest() {
        try {
            new CacheStatsView(CacheType.LAW);
        }
        catch (IllegalArgumentException ignored) {
            Assert.fail("Cache " + CacheType.LAW + " does not have stats.");
        }
    }
}
