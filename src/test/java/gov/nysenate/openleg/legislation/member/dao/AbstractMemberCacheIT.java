package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.util.Tuple;
import gov.nysenate.openleg.legislation.AbstractCacheTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class AbstractMemberCacheIT<Key, Value> extends AbstractCacheTest<Key, Value> {
    protected static final Person samplePerson = new Person(-1,
            new PersonName("a", "b", "c", "d", "e", "f"), "g", "h");
    protected static final Member sampleMem = new Member(samplePerson, -2, Chamber.SENATE, false);
    protected static final SessionMember sampleSm = new SessionMember(-1, sampleMem, "ah",
            new SessionYear(1998), -3, false);
    protected static final FullMember sampleFm = new FullMember(List.of(sampleSm));

    @Override
    protected abstract AbstractMemberCache<Key, Value> getCachingService();

    protected abstract Tuple<Key, Value> getSampleData();

    protected void basicMemberTest() {
        Tuple<Key, Value> data = getSampleData();
        Key key = data.v1();
        Value value = data.v2();
        stats.clear();
        assertEquals(0, stats.getCacheMisses());
        try {
            getCachingService().getMember(key);
            fail("Cache should not have this id!");
        }
        catch (MemberNotFoundEx ignored) {
            assertEquals(1, stats.getCacheMisses());
        }

        assertEquals(0, stats.getCachePuts());
        cache.put(key, value);
        assertEquals(1, stats.getCachePuts());

        assertEquals(0, stats.getCacheHits());
        assertEquals(value, getCachingService().getMember(key));
        assertEquals(1, stats.getCacheHits());

//        assertEquals(0, stats.getCacheRemovals());
//        getCachingService()
//        assertEquals(1, stats.getCacheRemovals());
    }
}
