package gov.nysenate.openleg.libs;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class DiffMatchPatchTests {

    private static final DiffMatchPatch dmp = new DiffMatchPatch();

    @Test
    public void simpleCase() {
        var actual = dmp.diffMain("Hello world", "Hello earth");
        dmp.diffCleanupSemantic(actual);
        var expected = new LinkedList<DiffMatchPatch.Diff>();
        expected.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.EQUAL, "Hello "));
        expected.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.DELETE, "world"));
        expected.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.INSERT, "earth"));
        assertEquals(expected, actual);
    }

    /**
     * Diff's do not convert to string's well (i.e. when calling toString()) when there are carriage returns and line feeds.
     * However, the raw data seems accurate.
     */
    @Test
    public void lineFeedAndCarriageReturn() {
        var actual = dmp.diffMain("Hello\r\nworld", "Hello\r\nearth");
        dmp.diffCleanupSemantic(actual);
        var expected = new LinkedList<DiffMatchPatch.Diff>();
        expected.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.EQUAL, "Hello\r\n"));
        expected.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.DELETE, "world"));
        expected.add(new DiffMatchPatch.Diff(DiffMatchPatch.Operation.INSERT, "earth"));
        assertEquals(expected, actual);
    }
}
