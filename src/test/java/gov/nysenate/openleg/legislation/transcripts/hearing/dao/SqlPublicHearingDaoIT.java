package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class SqlPublicHearingDaoIT extends BaseTests {
    private static final String DIRECTORY = "src/test/resources/hearing/";

    @Autowired
    private PublicHearingFileDao hearingFileDao;
    @Autowired
    private PublicHearingDao hearingDao;

    @Test
    public void basicHostTest() throws FileNotFoundException {
        var sampleHost1 = new HearingHost(Chamber.SENATE, HearingHostType.COMMITTEE, "Labor");
        var sampleHost2 = new HearingHost(Chamber.ASSEMBLY, HearingHostType.WHOLE_CHAMBER, "");
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var testHearing = new PublicHearing("noDate.txt", "sample text", "sample title", "sample address", now.toLocalDate(),
                now.toLocalTime(), now.plusHours(1).toLocalTime());
        testHearing.setHosts(List.of(sampleHost1, sampleHost2));

        hearingFileDao.updatePublicHearingFile(new PublicHearingFile(new File(DIRECTORY + testHearing.getFilename())));
        hearingDao.updatePublicHearing(testHearing);

        PublicHearingId id = hearingDao.getPublicHearingIds(SortOrder.DESC, LimitOffset.ONE).get(0);
        testHearing.setId(id);
        var retrievedHearing = hearingDao.getPublicHearing(id);
        assertEquals(testHearing, retrievedHearing);
    }
}
