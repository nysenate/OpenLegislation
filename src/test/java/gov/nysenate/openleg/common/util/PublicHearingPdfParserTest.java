package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Make into unit test. Stop extending.
@Category(SillyTest.class)
public class PublicHearingPdfParserTest extends BaseTests {
    @Autowired
    private PublicHearingDataService hearings;

    @Test
    public void testHearings() {
        var pageLengthCount = new HashMap<Integer, Integer>();
        var ids = hearings.getPublicHearingIds(SortOrder.ASC, LimitOffset.ALL);
        // File name to count.
        for (var id : ids) {
            PublicHearing hearing = hearings.getPublicHearing(id);
            var pages = PublicHearingTextUtils.getPages(hearing.getText());
            for (int i = 0; i < pages.size(); i++) {
                List<String> page = pages.get(i);
                int size = page.size();
                pageLengthCount.put(size, pageLengthCount.getOrDefault(size, 0) + 1);
            }
        }
        System.out.println(pageLengthCount);
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
                "~", "%"};
        for (String s : puncArray)
            punctuation.add(s.codePointAt(0));
        String[] accentArray = {"ã", "à", "á", "é", "Í", "í", "ï", "î", "ó", "ò", "õ", "ô", "Ú", "ú", "ç", "Ñ", "ñ", "ý"};
        for (String s : accentArray)
            accentChars.add(s.codePointAt(0));
    }

    private static boolean printable(int codepoint) {
        return alphaNum.contains(codepoint) || accentChars.contains(codepoint) ||
                punctuation.contains(codepoint);
    }
}
