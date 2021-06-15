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

    List<String> toFix = List.of("1999-12-28T14:31", "2003-03-19T11:16", "1999-04-20T15:05", "2001-04-30T15:07", "2004-05-03T15:16", "2001-05-29T15:07", "1999-10-18T10:00", "2004-08-11T14:19", "2005-01-31T15:17", "2001-10-24T16:16", "2001-06-20T11:26", "2001-04-18T11:13", "2004-06-21T15:13", "2001-05-07T15:17", "2001-06-18T15:13", "1999-10-22T10:00", "2001-02-05T15:09", "2009-11-18T12:17", "1999-06-15T11:18", "1999-12-15T10:00", "2003-01-14T12:35", "2001-06-04T15:08", "1999-10-25T10:00", "1999-12-20T10:00", "1999-12-14T14:10", "1999-10-07T17:06", "2005-04-05T15:06", "2005-01-24T15:30", "1999-10-13T10:00", "1999-12-17T10:00", "2001-05-02T11:12", "1999-05-11T15:03", "2010-06-01T16:07", "1999-06-10T11:05", "1999-06-16T11:09", "2001-04-03T11:13", "2001-05-31T11:08", "2001-06-21T10:45", "1999-11-05T10:00", "2002-02-25T15:16", "2001-05-14T15:11", "2001-04-04T10:14", "2001-03-19T15:07", "2001-06-11T15:11", "1999-10-12T10:00", "2003-04-01T15:08", "1999-11-01T10:00", "2004-06-16T11:10", "1999-06-09T11:08", "1999-10-20T10:00", "2002-01-29T11:18", "2004-05-24T15:12", "1999-10-15T10:00", "2004-04-28T11:05", "2000-03-15T11:12", "2001-02-27T11:07", "1999-11-24T10:00", "2003-06-11T15:06", "2001-03-20T11:19", "2003-04-14T15:32", "2000-04-05T12:12", "2001-09-13T15:03", "2004-06-09T11:03");

    @Test
    public void fixTranscripts() {
        for (var dt : toFix) {
            Transcript t = transcripts.getTranscript(new TranscriptId(dt));
            String correctedText = t.getText().replaceAll("¦", "").replaceAll("«", "");
            Path p = Path.of("src/test/resources/transcriptFiles/fixedFiles/" + t.getDateTime().toString().replaceFirst(":", "") + ".fixed");
            try {
                Files.writeString(p, correctedText, StandardCharsets.ISO_8859_1, StandardOpenOption.CREATE);
            } catch (IOException e) {
                fail();
            }
        }
    }

    @Test
    public void checkTranscripts() {
        Set<String> badCodePoints = new HashSet<>();
        var ids = transcripts.getTranscriptIds(SortOrder.ASC, LimitOffset.ALL);
        for (var id : ids) {
            Transcript t = transcripts.getTranscript(id);
            var lines = t.getText().split("\n");
            for (var line : lines) {
                for (int codePoint : line.codePoints().toArray()) {
                    if (notPrintable(codePoint)) {
                        System.out.println(id.getDateTime());
                        System.err.println(line);
                        badCodePoints.add(id.getDateTime().toString());
                        break;
                    }
                }
            }
        }
        for (var bcp : badCodePoints) {
            System.out.print("\"" + bcp + "\", ");
        }
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
        String[] puncArray = {" ", ".", ",", "!", "?", "\n", ":", "-", "'", "(", ")", "\"", ";", "$", "/",
                "*", "&", "[", "]", "`", "{", "}", "#", "^", "=", "<", ">", "|", "_", "+", "\\", "½", "¡", "@",
                "~", "%", "\f"};
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
