package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfParser;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    static class PageNumData {
        public LocalDateTime ldt;
        public int start, end;

        public PageNumData(LocalDateTime ldt, int start, int end) {
            this.ldt = ldt;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "PageNumData{" +
                    "ldt=" + ldt +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    @Test
    public void transcriptPageNumTest() {
        var ids = transcripts.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);

        LocalDateTime ldt = LocalDateTime.parse("2009-06-22T10:00");
        ids = ids.stream().filter(id -> id.getDateTime().compareTo(ldt) < 0 || id.getDateTime().getYear() >= 2011)
                .collect(Collectors.toList());
        var numsList = new ArrayList<PageNumData>();
        // Compiles list of data.
        for (var id : ids) {
            var transcript = transcripts.getTranscript(id);
            List<List<String>> pages = new TranscriptPdfParser(id.getDateTime(), transcript.getText()).getPages();
            int firstPageNum = Integer.parseInt(pages.get(0).get(0));
            int lastPageNum = Integer.parseInt(pages.get(pages.size() - 1).get(0));
            numsList.add(new PageNumData(id.getDateTime(), firstPageNum, lastPageNum));
        }

        int count = 0;
        for (int i = 1; i < numsList.size(); i++) {
            var prev = numsList.get(i - 1);
            var curr = numsList.get(i);
            if (curr.start == 1)
                continue;
            int diff = curr.start - prev.end;
            // 3 is the minimum session transcript length.
            if (diff > 4) {
                System.out.println("Gap between " + prev + " and " + curr);
                count++;
            }
            if (diff < 0) {
                System.out.println("Negative gap between " + prev + " and " + curr);
                count++;
            }
        }
        System.err.println(count);
    }

}
