package gov.nysenate.openleg.service.entity.member;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.member.search.ElasticMemberSearchService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ElasticMemberSearchServiceTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(ElasticMemberSearchServiceTests.class);

    @Autowired ElasticMemberSearchService memberSearchService;

    @Test
    public void memberSearchTest() throws SearchException {
        SearchResults<SessionMember> memberSearchResults = memberSearchService.searchMembers("*", "", LimitOffset.ALL);
        logger.info("{}");
    }
}
