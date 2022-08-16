package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class TranscriptLineTest {
    private String[] lineTexts;
    private Object[] expected;

    @Test
    public void constructorTest() {
        lineTexts = new String[]{"Here's a line!", "", "A", "\fB"};
        expected = new String[]{lineTexts[0], "", "A", "B"};
        testHelper(TranscriptLine::text);
    }

    @Test
    public void testPageNumber() {
        lineTexts = new String[]{"                                 1234", "4321", "55", "                1397."};
        expected = new Object[]{true, false, false, false};
        testHelper(TranscriptLine::isPageNumber);
    }

    @Test
    public void testRemoveLineNumber() {
        lineTexts = new String[]{"        21   NEW YORK STATE SENATE ", "    22", "    2",
                "       THE NEW YORK SENATE", "               833"};
        expected = new Object[]{"NEW YORK STATE SENATE", "", "",
                "       THE NEW YORK SENATE", "               833"};
        testHelper(TranscriptLine::removeLineNumber);
    }

    @Test
    public void timeTypoNotInterpretedAsLineNumber() {
        TranscriptLine line = new TranscriptLine("           10 00 a.m.");
        assertTrue(line.getTime().isPresent());
        assertEquals(LocalTime.of(10, 0), line.getTime().get());
    }

    @Test
    public void testLocation() {
        lineTexts = new String[]{"  10      ALBANY, NEW YORK", "               ALBANY, NEW   YORK",
        "Something about New York and Albany"};
        expected = new Object[]{true, true, false};
        optionalTestHelper(TranscriptLine::getLocation, Optional::isPresent);

        lineTexts = new String[]{lineTexts[0], lineTexts[1]};
        expected = new Object[]{"ALBANY, NEW YORK", "ALBANY, NEW YORK"};
        optionalTestHelper(TranscriptLine::getLocation, Optional::get);
    }

    @Test
    public void testDateHandling() {
        // e.g 031699.v1
        TranscriptLine line = new TranscriptLine("   22             Albany NEW YORK");
        assertFalse(line.getDate().isPresent());
        lineTexts = new String[]{"    11          March 26, 2011", "                March 16, 1999.",
                "                February 28 , 1994", "                March 31,1993"};
        expected = new Object[]{LocalDate.of(2011, 3, 26), LocalDate.of(1999, 3, 16),
                LocalDate.of(1994, 2, 28), LocalDate.of(1993, 3, 31)};
        optionalTestHelper(TranscriptLine::getDate, Optional::get);
    }

    @Test
    public void testIsTime() {
        lineTexts = new String[]{"   13       26, 2011", "   9            9:55 am", "                10 00 a.m.",
                "                2:00 p.m.", "   9            12:55 pm", "             12:00 Noon"};
        expected = new Object[]{false, true, true, true, true, true};
        optionalTestHelper(TranscriptLine::getTime, Optional::isPresent);
    }

    @Test
    public void testGetTime() {
        TranscriptLine line = new TranscriptLine(" 10:15 a.m.");
        assertTrue(line.getTime().isPresent());
        assertEquals(LocalTime.of(10, 15), line.getTime().get());
    }

    @Test
    public void testIsSessionType() {
        lineTexts = new String[]{"   12           REGULAR SESSION", "               EXTRAORDINARY SESSION",
        "2                      Committee notices."};
        expected = new Object[]{true, true, false};
        optionalTestHelper(TranscriptLine::getSession, Optional::isPresent);
    }

    @Test
    public void testIsEmpty() {
        lineTexts = new String[]{"\t    \n", "       "};
        expected = new Object[]{true, true};
        testHelper(TranscriptLine::isBlank);
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

    /**
     * For use with any functions that return options.
     * @param lineFunction to apply to the TranscriptLine.
     * @param optionalFunction to apply to the result of f.
     */
    private void optionalTestHelper(Function<TranscriptLine, ?> lineFunction, Function<Optional<?>, ?> optionalFunction) {
        assertEquals(lineTexts.length, expected.length);
        for (int i = 0; i < lineTexts.length; i++) {
            TranscriptLine line = new TranscriptLine(lineTexts[i]);
            Optional<?> result = (Optional<?>) lineFunction.apply(line);
            assertEquals(expected[i], optionalFunction.apply(result));
        }
    }
}