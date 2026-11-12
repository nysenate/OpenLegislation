package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.util.Tuple;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
