package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.SessionType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class TranscriptFilenameInfoTest {
    @Test
    public void versionTest() {
        createAndTest("010318.v1.txt", "010318.v2.txt",
                LocalDate.of(2018, 1, 3), DayType.SESSION, "",
                false, false, true);
        createAndTest("Corrected-Senate090121ExSession.txt", "Senate090121ExSession.txt",
                LocalDate.of(2021, 9, 1), DayType.SESSION, "Extraordinary Session",
                false, false, false);
        createAndTest("TEST", "TEST.fixed",
                LocalDate.now(), null, "",
                true, true, true);
        createAndTest("010101.txt", "010201.txt",
                LocalDate.of(1, 1, 1), null, "",
                false, true, false);
        var same = new TranscriptFilenameInfo("TEST", LocalDate.now(), null, "");
        test(same, same, true, true, false);
        var legInName = new TranscriptFilenameInfo("SenateLD042022.txt", LocalDate.of(2022, 4, 20),
                DayType.SESSION, "");
        assertTrue(legInName.getMismatches().isPresent());
        var id = new TranscriptId(LocalDate.of(1993, 3, 17).atStartOfDay(), new SessionType("Regular Session"));
        var nullDayType = new Transcript(id, null, "SenateLD031793.v1", null, null);
        var nullDayTypeInfo = new TranscriptFilenameInfo(nullDayType);
        assertTrue(nullDayTypeInfo.getMismatches().isPresent());
    }

    private static void createAndTest(String filename1, String filename2, LocalDate date, DayType dayType, String sessionType,
                                      boolean expectMismatch1, boolean expectMismatch2, boolean expectLessAccurate) {
        test(new TranscriptFilenameInfo(filename1, date, dayType, sessionType),
                new TranscriptFilenameInfo(filename2, date, dayType, sessionType),
                expectMismatch1, expectMismatch2, expectLessAccurate);
    }

    private static void test(TranscriptFilenameInfo one, TranscriptFilenameInfo two,
                             boolean expectMismatch1, boolean expectMismatch2, boolean expectLessAccurate) {
        assertEquals(one.getMismatches().isPresent(), expectMismatch1);
        assertEquals(two.getMismatches().isPresent(), expectMismatch2);
        assertEquals(expectLessAccurate, one.isLessAccurateThan(two));
    }
}
