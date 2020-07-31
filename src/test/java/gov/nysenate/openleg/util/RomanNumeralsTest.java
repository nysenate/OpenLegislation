package gov.nysenate.openleg.util;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class RomanNumeralsTest {
    @Test
    public void numeralTest() {
        test("I", 1);
        test("IV", 4);
        test("XII", 12);
        test("XXIX", 29);
        test("XXXV", 35);
        test("XL", 40);
        test("XCIX", 99);
    }

    private static void test(String numeral, int num) {
        assertEquals(RomanNumerals.numeralToInt(numeral), num);
        assertEquals(numeral, RomanNumerals.intToNumeral(num));
    }
}
