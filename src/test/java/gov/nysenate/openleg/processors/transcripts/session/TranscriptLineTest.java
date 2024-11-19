package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TranscriptLineTest {
    private String[] lineTexts;
    private Object[] expected;

    @Test
    public void constructorTest() {
        lineTexts = new String[]{"Here's a line!", "", "A", "\fB"};
        expected = new String[]{lineTexts[0], "", "A", "B"};
        testHelper(TranscriptLine::getText);
    }

    @Test
    public void testIsStenographer() {
        lineTexts = new String[]{"  Candyco Transcription Service, Inc. ", " (518) 371-8910 "};
        expected = new Object[]{true, true};
        testHelper(TranscriptLine::isStenographer);
    }

    private void testHelper(Function<TranscriptLine, ?> lineFunction) {
        assertEquals(lineTexts.length, expected.length);
        for (int i = 0; i < lineTexts.length; i++) {
            TranscriptLine line = new TranscriptLine(lineTexts[i]);
            assertEquals(expected[i], lineFunction.apply(line));
        }
    }
}