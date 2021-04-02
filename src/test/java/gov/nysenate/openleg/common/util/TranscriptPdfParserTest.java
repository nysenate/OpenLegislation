package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.elasticsearch.common.collect.Tuple;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

// TODO: to UnitTest
@Category(IntegrationTest.class)
public class TranscriptPdfParserTest extends BaseTests {
    private static final String TEST_FILE_DIR = "src/test/resources/transcriptFiles/";
    private static final String NORMAL_TRANSCRIPT = "2020-01-01T1100.v1";

    private static final Set<String> TO_EXAMINE = Set.of();

    private static final Set<String> FIXED = Set.of("1995-01-26T10:00", "1998-01-07T12:15", "1998-03-10T15:10",
            "1998-04-08T15:00", "1998-04-29T15:10", "1998-05-06T11:12", "1998-05-11T15:02", "1998-05-12T15:04",
            "1998-05-20T11:04", "1999-01-25T15:08", "1998-05-26T15:05", "1994-03-24T12:01", "1994-05-11T10:00",
            "1994-05-24T16:15", "1994-06-07T14:20", "1994-06-15T12:05", "1995-04-04T10:05", "1995-06-02T10:05",
            "1995-06-13T10:01", "1995-06-28T10:03", "1996-01-04T10:00", "1996-01-05T10:00", "1996-02-29T10:00",
            "1996-03-01T10:00", "1996-03-02T10:00", "1996-03-03T10:00", "1996-03-31T10:00", "1996-04-21T10:00",
            "1996-05-09T10:00", "1996-05-26T10:00", "1996-06-07T10:00", "1996-06-08T10:00", "1996-06-13T10:03",
            "1996-06-25T10:00", "1996-06-26T10:00", "1996-06-27T10:00", "1996-06-29T10:00", "1997-01-09T10:00",
            "1997-02-18T10:00", "1996-01-09T14:01", "1998-05-13T11:05", "1999-01-12T11:05", "1999-02-01T15:10",
            "1999-02-02T11:07", "1999-03-02T15:06");

    //TODO: see 1996-01-09T14:01, should tabs go to 9 spaces?
    private static final Set<String> otherDiffLengths = Set.of("1994-06-30T11:44", "1997-01-23T10:00", "1998-03-10T15:10",
            "1998-04-08T15:00", "1998-04-29T15:10", "1998-05-06T11:12", "1998-05-11T15:02", "1998-05-12T15:04",
            "1998-05-13T11:05", "1998-05-20T11:04", "1998-05-26T15:05", "2012-10-10T14:00");

    // TODO: remove this, and related test.
    @Autowired
    private TranscriptDataService transcriptDataService;

    // TODO: check new page length stays consistent.
    @Test
    public void checkTranscripts() {
        List<TranscriptId> ids = transcriptDataService.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        ids = ids.stream().filter(id -> !FIXED.contains(id.getDateTime().toString())).collect(Collectors.toList());
        ids = ids.stream().filter(id -> id.getDateTime().getYear() <= 2007).collect(Collectors.toList());
        var pageCountMismatches = new ArrayList<String>();
        var pageLengthMismatches = new ArrayList<String>();
        var textMismatches = new ArrayList<String>();

        for (TranscriptId id : ids) {
//            if (!TO_EXAMINE.contains(id.getDateTime().toString()))
//                continue;
            Transcript t = transcriptDataService.getTranscript(id);
            var oldText = TranscriptTextUtils.getPdfFormattedPages(t.getText());
            var newText = new TranscriptPdfParser(t.getText()).getPages();
            if (oldText.size() != newText.size()) {
                pageCountMismatches.add(id.getDateTime().toString());
                continue;
            }
            // Now, onto analyzing a single page.
            for (int j = 0; j < oldText.size(); j++) {
                List<String> oldPage = oldText.get(j);
                List<String> newPage = newText.get(j);
                if (oldPage.size() != newPage.size()) {
                    pageLengthMismatches.add(id.getDateTime().toString());
                    continue;
                }
                // Lastly, analyze a single line.
                for (int k = 0; k < oldPage.size(); k++) {
                    String oldLine = oldPage.get(k).stripTrailing();
                    String newLine = newPage.get(k);
                    if (k == 0)
                        oldLine = oldLine.trim();
                    if (!oldLine.equals(newLine))
                        textMismatches.add(id.getDateTime().toString());
                }
            }
        }
        System.out.println("Page count mismatches:");
        for (String s : pageCountMismatches)
            System.out.print("\"" + s + "\", ");

        System.out.println("\n\nPage length mismatches:");
        for (String s : pageLengthMismatches)
            System.out.print("\"" + s + "\", ");

        System.out.println("\n\nText mismatches:");
        for (String s : textMismatches)
            System.out.print("\"" + s + "\", ");
        System.out.println();
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
    public void lookForPatterns() {
        Deque<Tuple<String, Integer>> dateTimeToPageLength = new LinkedList<>();
        Set<Integer> pageLengths = new HashSet<>();
        var firstPageDifferentLength = new ArrayList<String>();
        List<TranscriptId> ids = transcriptDataService.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        for (TranscriptId id : ids) {
            Transcript t = transcriptDataService.getTranscript(id);
            if (otherDiffLengths.contains(t.getDateTime().toString()))
                continue;
            var pages = TranscriptTextUtils.getPdfFormattedPages(t.getText());
            int firstPageLength = pages.get(0).size();
            int secondPageLength = pages.get(1).size();
            if (firstPageLength != secondPageLength)
                firstPageDifferentLength.add(t.getDateTime().toString());
            else {
                if (pages.size() < 3)
                    continue;
                pageLengths.add(secondPageLength);
                if (dateTimeToPageLength.isEmpty() || dateTimeToPageLength.peekLast().v2() != secondPageLength)
                    dateTimeToPageLength.add(new Tuple<>(t.getDateTime().toString(), secondPageLength));
            }
        }

        System.out.println("\n**********");
        for (var pair : dateTimeToPageLength)
            System.out.println(pair.v1() + ", " + pair.v2());
        System.out.println("**********\n");
        System.out.println(pageLengths);
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
