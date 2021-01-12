package gov.nysenate.openleg.common.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * A class that converts between Roman numerals and integers.
 */
public class NumberUtils {
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

    private NumberUtils(){}

    /** For use in number to word conversion. */
    private static final HashMap<Integer, String> NUMBER_WORDS = new HashMap<>();
    static {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9,
                11, 12, 13, 14, 15, 16, 17, 18, 19};
        String[] words = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE",
                "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"};
        for (int i = 0; i < nums.length; i++)
            NUMBER_WORDS.put(nums[i], words[i]);
        String[] tens = {"TEN", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY", "ONE HUNDRED"};
        for (int i = 0; i < tens.length; i++)
            NUMBER_WORDS.put(10*(i+1), tens[i]);
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

    /**
     * Quickly converts a number 1-199 to a word or words.
     * @param number to convert.
     * @return a word/phrase.
     */
    public static String intToWord(int number) {
        int tens = number/10;
        if (tens < 2 || number%10 == 0)
            return NUMBER_WORDS.getOrDefault(number, "no word");
        else if (tens >= 10)
            return NUMBER_WORDS.get(100) + " " + intToWord(number-100);
        else
            return NUMBER_WORDS.get(10 * tens) + "-" + intToWord(number - 10 * tens);
    }

    public static String allOptions(String stringNum) {
        int num = Integer.parseInt(stringNum);
        return "(" + stringNum + "|" + intToNumeral(num) + "|" + intToWord(num) + ")";
    }
}
