package gov.nysenate.openleg.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TranscriptLineTest {

    @Test
    public void testTranscriptNumber() {
        TranscriptLine line = new TranscriptLine("                                 1234");
        assertTrue(line.isTranscriptNumber());

        line = new TranscriptLine("4321");
        assertTrue(line.isTranscriptNumber());

        // Sometimes transcript number starts the second line. e.g. 011299.v1, 020597.v1
        line = new TranscriptLine("55");
        assertTrue(line.isTranscriptNumber());

        // Remove broken pipe character. e.g. 122099.v1
        line = new TranscriptLine("  �                               2301");
        assertTrue(line.isTranscriptNumber());
    }

    @Test
    public void testRemoveLineNumber() {
        TranscriptLine line = new TranscriptLine("        21   NEW YORK STATE SENATE ");
        assertEquals("   NEW YORK STATE SENATE", line.removeLineNumber());

        line = new TranscriptLine("    22");
        assertEquals("", line.removeLineNumber().trim());

        line = new TranscriptLine("    2");
        assertEquals("", line.removeLineNumber().trim());

        line = new TranscriptLine("       THE NEW YORK SENATE");
        assertEquals("       THE NEW YORK SENATE", line.removeLineNumber());
    }

    @Test
    public void timeTypoNotInterpretedAsLineNumber() {
        TranscriptLine line = new TranscriptLine("           10 00 a.m.");
        assertEquals("1000am", line.getTimeString());
    }

    @Test
    public void testIsLocation() {
        TranscriptLine line = new TranscriptLine("  10      ALBANY, NEW YORK");
        assertTrue(line.isLocation());

        // e.g. 021197.v1
        line = new TranscriptLine("               ALBANY, NEW   YORK");
        assertTrue(line.isLocation());
    }

    @Test
    public void testDateHandling() {
        TranscriptLine line = new TranscriptLine("             Mar 26, 2011");
        assertTrue(line.isDate());
        assertEquals("Mar 26 2011", line.getDateString());

        // e.g 031699.v1
        line = new TranscriptLine("                Mar 16, 1999.");
        assertTrue(line.isDate());
        assertEquals("Mar 16 1999", line.getDateString());

        // e.g. 022894.v1
        line = new TranscriptLine("                Feb 28 , 1994");
        assertTrue(line.isDate());
        assertEquals("Feb 28 1994", line.getDateString());

        // e.g. 033193.v1
        line = new TranscriptLine("                Mar 31,1993");
        assertTrue(line.isDate());
        assertEquals("Mar 31 1993", line.getDateString());
    }

    @Test
    public void testIsTime() {
        TranscriptLine line = new TranscriptLine("  13       26, 2011");
        assertFalse(line.isTime());

        line = new TranscriptLine("   9            9:55 am");
        assertTrue(line.isTime());

        // e.g. 032611v1.TXT
        line = new TranscriptLine("                10 00 a.m.");
        assertTrue(line.isTime());

        line = new TranscriptLine("                2:00 p.m.");
        assertTrue(line.isTime());

        line = new TranscriptLine("   9            12:55 pm");
        assertTrue(line.isTime());

        // e.g. 030393.v1
        line = new TranscriptLine("             12:00 Noon");
        assertTrue(line.isTime());
    }

    @Test
    public void testGetTime() {
        TranscriptLine line = new TranscriptLine(" 10:15 a.m.");
        assertEquals("1015am", line.getTimeString());
    }

    @Test
    public void testIsSessionType() {
        TranscriptLine line = new TranscriptLine("   12           REGULAR SESSION");
        assertTrue(line.isSession());

        line = new TranscriptLine("               EXTRAORDINARY SESSION");
        assertTrue(line.isSession());
    }

    @Test
    public void testIsEmpty() {
        TranscriptLine line = new TranscriptLine("\t    \n");
        assertTrue(line.isEmpty());

        line = new TranscriptLine("       ");
        assertTrue(line.isEmpty());
    }

    @Test
    public void testIsStenographer() {
        TranscriptLine line = new TranscriptLine("  Candyco Transcription Service, Inc. ");
        assertTrue(line.isStenographer());

        line = new TranscriptLine(" (518) 371-8910 ");
        assertTrue(line.isStenographer());
    }

    @Test
    public void testRemoveInvalidCharacters() {
        TranscriptLine line = new TranscriptLine("  �                               2301");
        assertEquals("2301", line.removeInvalidCharacters());
    }
}
