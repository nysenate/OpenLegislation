package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.elasticsearch.core.Tuple;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(IntegrationTest.class)
public class SessionMemberIdCacheIT extends AbstractMemberCacheIT<Integer, SessionMember> {
    @Autowired
    private SessionMemberIdCache sessionMemberIdCache;

    @Override
    protected AbstractMemberCache<Integer, SessionMember> getCachingService() {
        return sessionMemberIdCache;
    }

    @Override
    protected Tuple<Integer, SessionMember> getSampleData() {
        return new Tuple<>(sampleSm.getSessionMemberId(), sampleSm);
    }

    @Test
    public void tests() {
        basicMemberTest();
    }
}
