package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static gov.nysenate.openleg.api.legislation.law.view.LawCharBlockType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category(UnitTest.class)
public class LawCharBlockTypeTest {
    @Test
    public void getMatcherTest() {
        String toMatch = "0 2 4";
        Matcher m = getMatcher(toMatch);
        for (int i = 0; i < toMatch.length(); i++) {
            if (!m.find())
                fail();
            if (i%2 == 0)
                assertEquals(Integer.toString(i), m.group());
            else
                assertEquals(" ", m.group());
        }
    }

    @Test
    public void addBoldingTest() {
        String[] parts = {"Not bolded, ", "this is bolded, ", "this is not"};
        StringBuilder sb = new StringBuilder(parts[0]);
        int start = sb.length();
        sb.append(parts[1]);
        int end = sb.length();
        sb.append(parts[2]);
        String bolded = addBoldMarkers(start, end, sb.toString());

        List<LawCharBlockType> expectedTypes = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            // Bolding markers should be added in-between the strings.
            if (i != 0)
                expectedTypes.add(BOLD_MARKER);
            Matcher m = getMatcher(parts[i]);
            while (m.find())
                expectedTypes.add(parseType(m.group()));
        }

        List<LawCharBlockType> actualTypes = new ArrayList<>();
        Matcher m = getMatcher(bolded);
        while (m.find())
            actualTypes.add(parseType(m.group()));

        assertEquals(expectedTypes.size(), actualTypes.size());
        for (int i = 0; i < actualTypes.size(); i++)
            assertEquals(expectedTypes.get(i), actualTypes.get(i));
    }

    @Test
    public void parseTypeTest() {
        String[] parts = {"ABCD", " \n\t"};
        LawCharBlockType[] expectedTypes = {BOLD_MARKER, ALPHANUM, BOLD_MARKER,
                SPACE, NEWLINE, SPACE};
        StringBuilder sb = new StringBuilder(parts[0]);
        int end = sb.length();
        sb.append(parts[1]);
        // Bolding the letters.
        String bolded = addBoldMarkers(0, end, sb.toString());
        Matcher m = getMatcher(bolded);
        List<LawCharBlockType> types = new ArrayList<>();
        while (m.find())
            types.add(parseType(m.group()));

        assertEquals(expectedTypes.length, types.size());
        for (int i = 0; i < expectedTypes.length; i++)
            assertEquals(expectedTypes[i], types.get(i));
    }
}
