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
            if (i != 0)
                expectedTypes.add(BOLDMARKER);
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
    public void currTypeTest() {
        String[] parts = {"ABCD", " \n\t"};
        StringBuilder sb = new StringBuilder(parts[0]);
        int end = sb.length();
        sb.append(parts[1]);

        String bolded = addBoldMarkers(0, end, sb.toString());
        Matcher m = getMatcher(bolded);
        List<LawCharBlockType> types = new ArrayList<>();
        while (m.find())
            types.add(parseType(m.group()));

        assertEquals(6, types.size());
        assertEquals(BOLDMARKER, types.get(0));
        assertEquals(ALPHANUM, types.get(1));
        assertEquals(BOLDMARKER, types.get(2));
        assertEquals(SPACE, types.get(3));
        assertEquals(NEWLINE, types.get(4));
        assertEquals(SPACE, types.get(5));
    }
}
