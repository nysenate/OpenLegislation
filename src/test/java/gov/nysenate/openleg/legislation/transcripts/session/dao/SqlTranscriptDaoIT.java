package gov.nysenate.openleg.legislation.transcripts.session.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.transcripts.session.*;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateToken;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class SqlTranscriptDaoIT extends BaseTests {
    private static final int NUM_TRANSCRIPTS = 3;
    private static final String FILEPATH = "src/test/resources/transcriptFiles/";
    private static final List<Transcript> TRANSCRIPTS = new ArrayList<>();
    private static final List<TranscriptFile> TRANSCRIPT_FILES = new ArrayList<>();
    private static TranscriptFile UPDATE_FILE;
    private static Transcript UPDATE;

    @Autowired
    private TranscriptDao dao;
    @Autowired
    private TranscriptFileDao fileDao;

    @BeforeClass
    public static void setup() throws FileNotFoundException {
        for (int i = 0; i < NUM_TRANSCRIPTS; i++) {
            LocalDateTime ldt = LocalDate.of(2020, Month.JULY, 30).atStartOfDay().plusHours(i);
            Transcript curr = new Transcript(new TranscriptId(ldt, new SessionType("REGULAR SESSION")),
                    DayType.SESSION, "t" + i + ".txt", "NYNY", "the text " + i);
            TRANSCRIPTS.add(curr);
            TRANSCRIPT_FILES.add(new TranscriptFile(new File(FILEPATH + curr.getFilename())));
        }
        Transcript curr = TRANSCRIPTS.get(0);
        UPDATE = new Transcript(curr.getId(), DayType.SESSION, "t0v1.txt",
                curr.getLocation(), curr.getText() + "v1");
        UPDATE_FILE = new TranscriptFile(new File(FILEPATH + UPDATE.getFilename()));
    }

    @Test
    public void getTranscriptIdsTest() {
        for (int i = 0; i < NUM_TRANSCRIPTS; i++) {
            fileDao.updateFile(TRANSCRIPT_FILES.get(i));
            dao.updateTranscript(TRANSCRIPTS.get(i));
        }
        List<TranscriptId> ids = getNewIds(dao.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL));
        assertEquals(TRANSCRIPTS.size(), ids.size());
        for (int i = 0; i < TRANSCRIPTS.size(); i++)
            assertEquals(TRANSCRIPTS.get(i).getId(), ids.get(i));
        List<Transcript> reversed = Lists.reverse(TRANSCRIPTS);
        ids = getNewIds(dao.getTranscriptIds(SortOrder.DESC, LimitOffset.ALL));
        assertEquals(TRANSCRIPTS.size(), ids.size());
        for (int i = 0; i < reversed.size(); i++)
            assertEquals(reversed.get(i).getId(), ids.get(i));
    }

    @Test
    public void getTranscriptTest() {
        fileDao.updateFile(TRANSCRIPT_FILES.get(0));
        dao.updateTranscript(TRANSCRIPTS.get(0));
        assertEquals(TRANSCRIPTS.get(0), dao.getTranscript(TRANSCRIPTS.get(0).getId()));
    }

    @Test
    public void updateTranscriptTest() {
        fileDao.updateFile(TRANSCRIPT_FILES.get(0));
        dao.updateTranscript(TRANSCRIPTS.get(0));
        fileDao.updateFile(UPDATE_FILE);
        dao.updateTranscript(UPDATE);
        assertEquals(UPDATE, dao.getTranscript(TRANSCRIPTS.get(0).getId()));
    }

    @Test
    public void transcriptsUpdatedDuringTest() {
        List<LocalDateTime> rangePoints = new ArrayList<>();
        for (int i = 0; i < NUM_TRANSCRIPTS; i++) {
            rangePoints.add(LocalDateTime.now());
            fileDao.updateFile(TRANSCRIPT_FILES.get(i));
            dao.updateTranscript(TRANSCRIPTS.get(i));
        }
        fileDao.updateFile(UPDATE_FILE);
        dao.updateTranscript(UPDATE);

        List<TranscriptUpdateToken> results = dao.transcriptsUpdatedDuring(
                Range.closed(rangePoints.get(0), LocalDateTime.now()),
                SortOrder.DESC, LimitOffset.ALL).results();

        assertEquals(3, results.size());

        assertEquals(TRANSCRIPTS.get(0).getDateTime(), results.get(0).getTranscriptId().dateTime());
    }

    /**
     * Only returns the new Transcripts added in the test.
     * @param list of all Transcripts in the database.
     * @return the relevant Transcripts.
     */
    private static List<TranscriptId> getNewIds(List<TranscriptId> list) {
        LocalDateTime start = TRANSCRIPTS.get(0).getDateTime();
        LocalDateTime end = TRANSCRIPTS.get(TRANSCRIPTS.size()-1).getDateTime();
        List<TranscriptId> ret = new ArrayList<>();
        for (TranscriptId id : list) {
            LocalDateTime currLDT = id.dateTime();
            if (currLDT.equals(start) || currLDT.equals(end))
                ret.add(id);
            else if (currLDT.isAfter(start) && currLDT.isBefore(end))
                ret.add(id);
        }
        return ret;
    }
}
