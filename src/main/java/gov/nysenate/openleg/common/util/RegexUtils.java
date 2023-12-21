package gov.nysenate.openleg.common.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains some methods to help create and use regular expressions.
 */
public final class RegexUtils {
    private static final String DELIM = "~~@@@@~~";

    private RegexUtils() {}

    /**
     * Allows a pattern to be matched, but to be maintained after splitting.
     * @param data to parse.
     * @param pattern the first in the tuple.
     * @return a List of Tuples where the first elements are what matches the pattern,
     * and the second elements are everything between those matches.
     */
    public static List<Pair<String>> specialSplit(String data, String pattern) {
        data = data.replaceAll(pattern, DELIM + "$0" + DELIM);
        ArrayList<String> dataList = new ArrayList<>(List.of(data.split(DELIM)));
        // After starting text, need an even number of elements to make Tuples.
        if (dataList.size()%2 != 1)
            dataList.add("");
        var pairList = new ArrayList<Pair<String>>();
        // Skips stuff before first match.
        for (int i = 1; i < dataList.size(); i += 2)
            pairList.add(new Pair<>(dataList.get(i), dataList.get(i + 1)));
        return pairList;
    }

    public static String removeAccentedCharacters(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", "");
    }
}
