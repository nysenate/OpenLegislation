package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.AbstractCacheTest;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.Person;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@IntegrationTest
public class FullMemberIdCacheIT extends AbstractCacheTest<Integer, FullMember> {
    @Autowired
    private FullMemberIdCache cache;

    @Override
    protected CachingService<Integer, FullMember> getCachingService() {
        return cache;
    }

    @Test
    public void putMemberTest() {
        int memberId = -1;
        int personId = -2;
        stats.clear();
        try {
            assertEquals(0, stats.getCacheMisses());
            cache.getMember(memberId);
            assertEquals(1, stats.getCacheMisses());
            fail("Cache should not have this id!");
        }
        catch (MemberNotFoundEx ignored) {}
        var sessionMember = new SessionMember(memberId, new SessionYear(1998));
        sessionMember.setMember(new Member(new Person(personId), memberId, Chamber.SENATE, false));
        sessionMember.setLbdcShortName("shortname");

        FullMember sampleFm = new FullMember(List.of(sessionMember));
        sampleFm.setPersonId(personId);
        assertEquals(0, stats.getCachePuts());
        putPair(memberId, sampleFm);
        assertEquals(sampleFm, cache.getMember(memberId));
        assertEquals(1, stats.getCachePuts());
        assertEquals(0, stats.getCacheRemovals());
        cache.evictContent(memberId);
        assertEquals(1, stats.getCacheRemovals());
        // TODO: hits?
    }
}
