package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.fail;

// TODO: Make into unit test. Stop extending.
@Category(SillyTest.class)
public class PublicHearingPdfParserTest extends BaseTests {
    @Autowired
    private PublicHearingDataService hearings;
    @Autowired
    private TranscriptDataService transcripts;

    private static final Set<String> TO_FIX =Set.of("2010-07-09T10:00");

    @Test
    public void fixTranscripts() {
        Set<String> badCodePoints = new HashSet<>();
//        var ids = transcripts.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
//        for (var id : ids) {
//            Transcript t = transcripts.getTranscript(id);
//            for (int codePoint : t.getText().codePoints().toArray()) {
//                if (codePoint == 160)
//                    badCodePoints.add(id.getDateTime().toString());
//            }
//        }


            Transcript t = transcripts.getTranscript(new TranscriptId("2010-07-09T10:00"));
            String correctedText = t.getText().replaceAll(Character.toString(27), "");
            Path p = Path.of("src/test/resources/transcriptFiles/" + t.getDateTime().toString().replaceFirst(":", "") + ".fixed");
            try {
                Files.writeString(p, correctedText, StandardCharsets.ISO_8859_1, StandardOpenOption.CREATE);
            }
            catch (IOException e) {
                fail();
            }


    }

    @Test
    public void testTranscripts() {
        Set<String> badCodePoints = new HashSet<>();
        var ids = transcripts.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        for (var id : ids) {
            Transcript t = transcripts.getTranscript(id);
            for (int codePoint : t.getText().codePoints().toArray()) {
                if (notPrintable(codePoint))
                    badCodePoints.add(id.toString());
            }
        }

//        for (var id : TO_FIX) {
//            Transcript t = transcripts.getTranscript(new TranscriptId(id));
//            var pages = new TranscriptPdfParser(t.getDateTime(), t.getText()).getPages();
//            for (var page : pages) {
//                for (var line : page) {
//                    for (int codePoint : line.codePoints().toArray()) {
//                        if (notPrintable(codePoint))
//                            System.err.println();
//                    }
//                }
//            }
//        }
        System.out.println(badCodePoints);
    }

    @Test
    public void testHearings() {
        var ids = hearings.getPublicHearingIds(SortOrder.ASC, LimitOffset.ALL);
        int badCharCount = 0;
        // File name to count.
        for (var id : ids) {
            PublicHearing hearing = hearings.getPublicHearing(id);
            List<List<String>> pages = PublicHearing.getPages(hearing.getText());
            for (int pageNum = 1; pageNum <= pages.size(); pageNum++) {
                var page = pages.get(pageNum - 1);
                for (int lineNum = 1; lineNum <= page.size(); lineNum++) {
                    var line = page.get(lineNum - 1);
                    var codePoints = line.codePoints().toArray();
                    for (int codePoint : codePoints) {
                        if (notPrintable(codePoint)) {
                            System.err.println("Bad char in " + hearing.getId().getFileName() +
                                    " at page " + pageNum + " at line " + lineNum);
                            System.out.println(++badCharCount);
                        }
                    }
                }
            }
        }
    }

    static Set<Integer> alphaNum = new HashSet<>(), punctuation = new HashSet<>(), accentChars = new HashSet<>();
    static {
        for (int i = 'a'; i <= 'z'; i++)
            alphaNum.add(i);
        for (int i = 'A'; i <= 'Z'; i++)
            alphaNum.add(i);
        for (int i = '0'; i <= '9'; i++)
            alphaNum.add(i);
        String[] puncArray = {" ", ".", ",", "!", "?", "\n", "\t", ":", "-", "'", "(", ")", "\"", ";", "$", "/", "¦",
                "*", "&", "[", "]", "`", "{", "}", "#", "^", "=", "<", ">", "|", "_", "+", "\\", "½", "¡", "@", "«",
                "~", "%", "\f", "\r"};
        for (String s : puncArray)
            punctuation.add(s.codePointAt(0));
        String[] accentArray = {"ã", "à", "á", "Á", "è", "é", "É", "Í", "í", "ï", "î", "ó", "ò", "õ", "ô", "Ú", "ú", "ç", "Ñ", "ñ", "ý"};
        for (String s : accentArray)
            accentChars.add(s.codePointAt(0));
    }

    private static boolean notPrintable(int codepoint) {
        return !alphaNum.contains(codepoint) && !accentChars.contains(codepoint) &&
                !punctuation.contains(codepoint);
    }
}
