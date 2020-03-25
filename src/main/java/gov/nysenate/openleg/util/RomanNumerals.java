package gov.nysenate.openleg.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.TreeMap;

/**
 * A class that converts between Roman numerals and integers.
 */
public class RomanNumerals {
    private static final BiMap<Integer, String> mapping;
    static {
        TreeMap<Integer, String> temp = new TreeMap<>((t0, t1) -> t1-t0);
        temp.put(100, "C");
        temp.put(90, "XC");
        temp.put(50, "L");
        temp.put(40, "XL");
        temp.put(10, "X");
        temp.put(9, "IX");
        temp.put(5, "V");
        temp.put(4, "IV");
        temp.put(1, "I");
        mapping = ImmutableBiMap.copyOf(temp);
    }

    /**
     * Converts a numeral to an int.
     * @param numeral to be converted.
     * @return the proper integer.
     */
    public static int numeralToInt(String numeral) {
        if (numeral.isEmpty())
            return 0;
        int numeralLength = (numeral.length() != 1 && mapping.inverse().containsKey(numeral.
                substring(0, 2))) ? 2 : 1;
        return mapping.inverse().get(numeral.substring(0, numeralLength)) + numeralToInt(numeral.
                substring(numeralLength));
    }

    /**
     * Converts an int to a numeral.
     * @param number to be converted.
     * @return the proper numeral.
     */
    public static String intToNumeral(int number) {
        if (number == 0)
            return "";
        int next = -1;
        for (int i : mapping.keySet()) {
            if (i <= number) {
                next = i;
                break;
            }
        }
        return mapping.get(next) + intToNumeral(number-next);
    }
}
