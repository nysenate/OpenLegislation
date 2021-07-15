package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.processors.transcripts.hearing.HearingHostParser;
import gov.nysenate.openleg.processors.transcripts.hearing.PublicHearingTextUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

// TODO: Make into unit test. Stop extending.
@Category(SillyTest.class)
public class PublicHearingPdfParserTest extends BaseTests {
    @Autowired
    private PublicHearingDataService hearings;
    @Autowired
    private TranscriptDataService transcripts;

    @Test
    public void regexTest() {
        String testStr = " ON LABOR; AND\n";
        testStr = testStr.replaceAll("\\s+", " ");
        String regex = "((,|AND|;)\\s?)+$";
        String replacement = "";
        System.out.println(testStr.replaceAll(regex, replacement));
    }

    @Test
    public void fullHearingHostTest() {
        var ids = hearings.getPublicHearingIds(SortOrder.ASC, LimitOffset.ALL);
        for (var id : ids) {
            PublicHearing hearing = hearings.getPublicHearing(id);
            PublicHearingTextUtils.getHearingFromText(id, hearing.getText(), new ArrayList<>());
        }
        for (var host : HearingHostParser.hosts)
            System.out.println(host);
    }
}
