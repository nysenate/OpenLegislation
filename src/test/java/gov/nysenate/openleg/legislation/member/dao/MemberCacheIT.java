package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.AbstractCacheTest;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class MemberCacheIT extends AbstractCacheTest<Integer, FullMember> {
    protected static final Person samplePerson = new Person(-1,
            new PersonName("a", "b", "c", "d"), "e", "f");
    protected static final Member sampleMem = new Member(samplePerson, -2, Chamber.SENATE, false);
    protected static final SessionMember sampleSm = new SessionMember(-1, sampleMem, "ah",
            new SessionYear(1998), -3, false);
    protected static final FullMember sampleFm = new FullMember(List.of(sampleSm));

    @Autowired
    private CachedMemberService cachingService;

    @Override
    protected CachingService<Integer, FullMember> getCachingService() {
        return cachingService;
    }

    @Test
    public void basicMemberTest() {
        stats.clear();
        assertEquals(0, stats.getCacheMisses());
        try {
            cachingService.getFullMemberById(sampleFm.getMemberId());
            fail("Cache should not have this id!");
        }
        catch (MemberNotFoundEx ignored) {
            assertEquals(1, stats.getCacheMisses());
        }

        assertEquals(0, stats.getCachePuts());
        cache.put(sampleFm.getMemberId(), sampleFm);
        assertEquals(1, stats.getCachePuts());

        assertEquals(0, stats.getCacheHits());
        assertEquals(sampleFm, cachingService.getFullMemberById(sampleFm.getMemberId()));
        assertEquals(1, stats.getCacheHits());
    }
}
