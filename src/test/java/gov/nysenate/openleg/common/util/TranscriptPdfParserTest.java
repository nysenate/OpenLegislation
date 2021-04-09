package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: to UnitTest
@Category(IntegrationTest.class)
public class TranscriptPdfParserTest extends BaseTests {
    private static final String TEST_FILE_DIR = "src/test/resources/transcriptFiles/";
    private static final String NORMAL_TRANSCRIPT = "2020-01-01T1100.v1";

    private static final Set<String> TO_EXAMINE = Set.of(),
    KNOWN_LENGTH_PROBLEMS = Set.of("1994-06-30T11:44", "1997-01-23T10:00", "1998-01-07T12:15", "1999-03-15T03:00",
            "2003-06-03T15:06", "2004-06-10T11:04", "2012-10-10T14:00");


    // TODO: see 1996-01-09T14:01, should tabs go to 9 spaces?

    // TODO: remove this, and related test.
    @Autowired
    private TranscriptDataService transcriptDataService;

    @Test
    public void checkLength() {
        List<TranscriptId> ids = transcriptDataService.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        Set<Integer> firstPageLengths = new HashSet<>();
        firstPageLengths.add(24);
        firstPageLengths.add(25);
        firstPageLengths.add(26);
        for (var id : ids) {
            Transcript t = transcriptDataService.getTranscript(id);
            if (KNOWN_LENGTH_PROBLEMS.contains(t.getDateTime().toString()))
                continue;
            var pages = new TranscriptPdfParser(t.getText()).getPages();
            if (pages.size() < 2) {
                System.out.println(t.getDateTime());
                continue;
            }

            int firstPageLength = pages.get(0).size();
            if (!firstPageLengths.contains(firstPageLength)) {
                System.out.println("Adding first page of length " + firstPageLength + " from " + t.getDateTime());
                firstPageLengths.add(firstPageLength);
            }

            int secondPageLength = pages.get(1).size();
            for (int i = 1; i < pages.size() - 1; i++) {
                int currPageLength = pages.get(i).size();
                if (secondPageLength != currPageLength)
                    System.out.println(t.getDateTime() + ", page " + (i + 1));
            }
        }
        System.out.println(firstPageLengths);
    }

    @Test
    public void simpleTranscriptTest() {
        Set<Integer> noLineYears = Set.of(1999, 2000, 2001, 2002, 2003, 2004);
        List<TranscriptId> ids = transcriptDataService.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        for (var id : ids) {
            Transcript curr = transcriptDataService.getTranscript(id);
            TranscriptPdfParser pdfParser = new TranscriptPdfParser(curr.getText());
            if (noLineYears.contains(curr.getYear())) {
                if (pdfParser.hasLineNumbers)
                    System.out.println("Has line numbers, but shouldn't: " + curr.getDateTime());
            }
            else {
                if (!pdfParser.hasLineNumbers)
                    System.out.println("Doesn't have line numbers, but should: " + curr.getDateTime());
            }
        }
    }

    @Test
    public void badCharacters() {
        List<TranscriptId> ids = transcriptDataService.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        Set<Integer> badCodePoints = new HashSet<>();
        for (var id : ids) {
            Transcript t = transcriptDataService.getTranscript(id);
            for (int codePoint : t.getText().codePoints().toArray()) {
                if (!printable(codePoint)) {
                    System.out.println("Bad character " + Character.toString(codePoint) + ", number " + codePoint + " in " + id);
                    badCodePoints.add(codePoint);
                }
            }
        }
        System.out.println(badCodePoints);
    }

    static int A = "A".codePointAt(0), Z = "Z".codePointAt(0),
            a = "a".codePointAt(0), z = "z".codePointAt(0),
            zero = "0".codePointAt(0), nine = "9".codePointAt(0);
    static Set<Integer> punctuation = new HashSet<>(), accentChars = new HashSet<>();
    static {
        String[] puncArray = {" ", ".", ",", "!", "?", "\n", "\t", ":", "-", "'", "(", ")", "\"", ";", "$", "/",
                "*", "&", "[", "]", "`", "{", "}", "#", "^", "=", "<", ">", "|", "_", "+", "\\", "½", "¡", "@"};
        for (String s : puncArray)
            punctuation.add(s.codePointAt(0));
        String[] accentArray = {"ã", "à", "á", "é", "Í", "í", "ï", "î", "ó", "ò", "õ", "ô", "Ú", "ú", "ç", "Ñ", "ñ", "ý"};
        for (String s : accentArray)
            accentChars.add(s.codePointAt(0));
    }

    private static boolean printable(int codepoint) {
        if (codepoint >= A && codepoint <= Z)
            return true;
        if (codepoint >= a && codepoint <= z)
            return true;
        if (codepoint >= zero && codepoint <= nine)
            return true;
        if (accentChars.contains(codepoint))
            return true;
        return punctuation.contains(codepoint);
    }

    @Test
    public void normalTranscriptText() throws IOException {
        for (var page : new TranscriptPdfParser(getText(NORMAL_TRANSCRIPT)).getPages()) {
            System.out.println("NEW PAGE");
            for (var line : page)
                System.out.println(line);
        }
    }

    // "1996-01-04T10:00", "1996-01-05T10:00", "1996-06-26T10:00", "1997-01-09T10:00"

    private static String getText(String filename) throws IOException {
        return Files.readString(Paths.get(TEST_FILE_DIR + filename));
    }
}
