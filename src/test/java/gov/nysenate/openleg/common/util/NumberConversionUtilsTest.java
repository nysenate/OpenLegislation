package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static gov.nysenate.openleg.common.util.NumberConversionUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Category(UnitTest.class)
public class NumberConversionUtilsTest {
    @Test
    public void numeralTest() {
        test("I", "ONE", 1);
        test("IV", "FOUR", 4);
        test("XII", "TWELVE", 12);
        test("XXIX", "TWENTY-NINE", 29);
        test("XXXV", "THIRTY-FIVE", 35);
        test("XL", "FORTY", 40);
        test("XCIX", "NINETY-NINE", 99);
        test("C", "ONE HUNDRED", 100);
        test("CLXIX", "ONE HUNDRED SIXTY-NINE", 169);
        assertThrows(NullPointerException.class, () -> numeralToInt("A"));
    }

    private static void test(String numeral, String word, int num) {
        assertEquals(num, numeralToInt(numeral));
        assertEquals(numeral, intToNumeral(num));
        assertEquals(word, intToWord(num));
        assertEquals("(" + num + "|" + numeral + "|" + word + ")", allOptions(Integer.toString(num)));
    }
}
