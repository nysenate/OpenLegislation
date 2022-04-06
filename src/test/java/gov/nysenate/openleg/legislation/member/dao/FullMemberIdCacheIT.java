package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.member.FullMember;
import org.elasticsearch.core.Tuple;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(IntegrationTest.class)
public class FullMemberIdCacheIT extends AbstractMemberCacheIT<Integer, FullMember> {
    @Autowired
    private FullMemberIdCache cachingService;

    @Override
    protected AbstractMemberCache<Integer, FullMember> getCachingService() {
        return cachingService;
    }

    @Override
    protected Tuple<Integer, FullMember> getSampleData() {
        return new Tuple<>(sampleFm.getMemberId(), sampleFm);
    }

    @Test
    public void tests() {
        basicMemberTest();
    }
}
