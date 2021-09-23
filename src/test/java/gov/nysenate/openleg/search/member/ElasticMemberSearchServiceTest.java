package gov.nysenate.openleg.search.member;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class ElasticMemberSearchServiceTest extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(ElasticMemberSearchServiceTest.class);

    @Autowired ElasticMemberSearchService memberSearchService;

    @Test
    public void memberSearchTest() throws SearchException {
        logger.info("{}", memberSearchService.searchMembers("*", "", LimitOffset.ALL));
    }
}
