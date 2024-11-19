package gov.nysenate.openleg.processors.transcripts.session;

import com.google.common.collect.Range;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfParser;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.Tuple;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptFilenameInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Maant to be run on a full database of transcripts.
 */
@Category(SillyTest.class)
public class FullTranscriptTests extends BaseTests {
    private static final String validChars = "[\\p{Graph} ½¾àãáçèëéÍíîïòõöôóÑñšÚúüý¡{}’ª\t]*";
    @Autowired
    private TranscriptDataService transcriptService;
    private List<Transcript> transcripts;

    @Before
    public void setup() {
        this.transcripts = transcriptService.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL).stream()
                .map(id -> transcriptService.getTranscript(id)).toList();
    }

    @Test
    public void pdfParserTest() {
        var dataList = new ArrayList<Tuple<Transcript, Range<Integer>>>();
        for (Transcript transcript : transcripts) {
            Range<Integer> pageNumRange = getPageNumRange(transcript);
            // Transcripts fitting this are numbered separately.
            if (!"Regular Session".equals(transcript.getSessionType()) &&
                    transcript.getDateTime().toLocalDate().isBefore(Stenographer.NONE.getStartDate())) {
                continue;
            }
            dataList.add(new Tuple<>(transcript, pageNumRange));
            if (dataList.size() % 700 == 0) {
                System.out.println(dataList.size() + " transcripts parsed.");
            }
        }
        sortData(dataList);

        var missingLegDays = new ArrayList<TranscriptId>();
        int expectedFirstPageNum = 1;
        for (var data : dataList) {
            int currYear = data.v1().getYear();
            int actualFirstPageNum = data.v2().lowerEndpoint();
            final int difference = actualFirstPageNum - expectedFirstPageNum;
            // One page is not enough room for an actual missing transcript.
            if (actualFirstPageNum != 1 && difference != 0 &&
                    // Many old transcripts are off by 1 page.
                    (currYear >= 2000 || Math.abs(difference) != 1)) {
                // As of 2024, we no longer process legislative day transcripts, which are 3 pages each.
                if (currYear < 2024) {
                    if (difference == 3) {
                        missingLegDays.add(data.v1().getId());
                    }
                    else {
                        System.err.printf("Page num should be %d, is %d in %s%n",
                                expectedFirstPageNum, actualFirstPageNum, data.v1().getId());
                    }
                }
                else if (difference % 3 != 0) {
                    System.err.println("Missing a session transcript near " + data.v1().getId());
                }
            }
            expectedFirstPageNum = data.v2().upperEndpoint() + 1;
        }

        if (!missingLegDays.isEmpty()) {
            System.err.println("\nThe following transcripts are missing a prior legislative day.");
            System.err.println(missingLegDays.stream().map(TranscriptId::toString).collect(Collectors.joining(", ")));
        }
    }

    @Test
    public void testFilenames() {
        for (Transcript transcript : transcripts) {
            var info = new TranscriptFilenameInfo(transcript);
            if (info.getMismatches().isPresent()) {
                System.err.println(transcript.getFilename() + " has issues:" + info.getMismatches().get());
            }
        }
    }

    @Test
    public void testEncoding() {
        for (Transcript transcript : transcripts) {
            var problemLines = transcript.getText().lines().map(line -> new TranscriptLine(line).getText())
                    .filter(text -> !text.matches(validChars)).toList();
            if (!problemLines.isEmpty()) {
                System.err.printf("Problems in %s:%n", transcript.getFilename());
                System.err.println(String.join("\n", problemLines));
                System.err.println("********************");
            }
        }
    }

    /**
     * Just ensures all PDFs can be generated.
     */
    @Test
    public void testPdfs() throws IOException {
        for (Transcript transcript : transcripts) {
            new TranscriptPdfView(transcript).writeData();
        }
    }

    private static Range<Integer> getPageNumRange(Transcript transcript) {
        List<List<String>> pages = new TranscriptPdfParser(transcript.getText()).getPages();
        final int firstPageNum = Integer.parseInt(pages.get(0).get(0));
        int currPageNum = firstPageNum;
        for (int i = 1; i < pages.size(); i++) {
            if (++currPageNum != Integer.parseInt(pages.get(i).get(0))) {
                System.err.printf("Page mismatch in %s near %d%n", transcript.getId(), currPageNum);
            }
        }
        return Range.closed(firstPageNum, currPageNum);
    }

    private static void sortData(List<Tuple<Transcript, Range<Integer>>> dataList) {
        dataList.sort(Comparator.comparing(tuple -> tuple.v1().getId()));
        for (int i = 0; i < dataList.size() - 1; i++) {
            var currData = dataList.get(i);
            var nextData = dataList.get(i + 1);
            LocalDate currDate = currData.v1().getDateTime().toLocalDate();
            // Some transcripts in this time were out of order.
            if (currDate.isAfter(LocalDate.of(2008, 8, 19)) &&
                    currDate.isBefore(LocalDate.of(2010, 6, 13)) &&
                    currDate.equals(nextData.v1().getDateTime().toLocalDate()) &&
                    !currData.v2().upperEndpoint().equals(nextData.v2().lowerEndpoint() - 1) &&
                    nextData.v2().lowerEndpoint() != 1) {
                dataList.set(i, nextData);
                dataList.set(i + 1, currData);
            }
        }
    }
}
