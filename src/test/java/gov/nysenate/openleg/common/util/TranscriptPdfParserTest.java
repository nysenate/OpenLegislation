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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: to UnitTest
@Category(IntegrationTest.class)
public class TranscriptPdfParserTest extends BaseTests {
    private static final String TEST_FILE_DIR = "src/test/resources/transcriptFiles/";
    private static final String NORMAL_TRANSCRIPT = "2020-01-01T1100.v1";
    private static final List<String> TO_EXAMINE = List.of();

    // TODO: see 1996-01-09T14:01, should tabs go to 9 spaces?
    // TODO: remove this, and related test.
    @Autowired
    private TranscriptDataService transcriptDataService;

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

    static Set<Integer> alphaNum = new HashSet<>(), punctuation = new HashSet<>(), accentChars = new HashSet<>();
    static {
        for (int i = "a".codePointAt(0); i <= "z".codePointAt(0); i++)
            alphaNum.add(i);
        for (int i = "A".codePointAt(0); i <= "Z".codePointAt(0); i++)
            alphaNum.add(i);
        for (int i = "0".codePointAt(0); i <= "9".codePointAt(0); i++)
            alphaNum.add(i);
        String[] puncArray = {" ", ".", ",", "!", "?", "\n", "\t", ":", "-", "'", "(", ")", "\"", ";", "$", "/", "¦",
                "*", "&", "[", "]", "`", "{", "}", "#", "^", "=", "<", ">", "|", "_", "+", "\\", "½", "¡", "@", "«"};
        for (String s : puncArray)
            punctuation.add(s.codePointAt(0));
        String[] accentArray = {"ã", "à", "á", "é", "Í", "í", "ï", "î", "ó", "ò", "õ", "ô", "Ú", "ú", "ç", "Ñ", "ñ", "ý"};
        for (String s : accentArray)
            accentChars.add(s.codePointAt(0));
    }

    private static boolean printable(int codepoint) {
        if (alphaNum.contains(codepoint) || accentChars.contains(codepoint))
            return true;
        return punctuation.contains(codepoint);
    }

    @Test
    public void normalTranscriptText() throws IOException {
        for (var page : new TranscriptPdfParser(LocalDateTime.parse(NORMAL_TRANSCRIPT), getText(NORMAL_TRANSCRIPT)).getPages()) {
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
