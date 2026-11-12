package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.util.Tuple;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SessionMemberNonIdCacheIT extends AbstractMemberCacheIT<ShortNameKey, SessionMember> {
    @Autowired
    private SessionMemberNonIdCache sessionMemberNonIdCache;

    @Override
    protected AbstractMemberCache<ShortNameKey, SessionMember> getCachingService() {
        return sessionMemberNonIdCache;
    }

    @Override
    protected Tuple<ShortNameKey, SessionMember> getSampleData() {
        return new Tuple<>(new ShortNameKey(sampleSm), sampleSm);
    }

    @Test
    public void tests() {
        basicMemberTest();
    }
}
