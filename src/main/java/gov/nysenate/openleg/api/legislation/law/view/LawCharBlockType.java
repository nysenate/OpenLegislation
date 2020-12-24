package gov.nysenate.openleg.api.legislation.law.view;

import java.util.regex.Pattern;

/**
 * An enum for storing the type of a small block of law text.
 */
public enum LawCharBlockType {
    BOLDMARKER("~~~~"), ALPHANUM("[^\\s~]+"), SPACE("[ \t]+"), NEWLINE("\n");

    private final String pattern;

    LawCharBlockType(String pattern) {
        this.pattern = pattern;
    }

    public static final Pattern LAW_CHAR_BLOCK_PATTERN;
    static {
        StringBuilder b = new StringBuilder();
        for (LawCharBlockType block : LawCharBlockType.values())
            b.append("(?<").append(block.name()).append(">").append(block.pattern).append(")|");
        // Removes dangling pipe character.
        LAW_CHAR_BLOCK_PATTERN = Pattern.compile(b.substring(0, b.length()-1));
    }

    /**
     * Adds markers fore bolding to a String
     * @param start of bolding.
     * @param end of bolding.
     * @param input to modified.
     * @return the "bolded" String.
     */
    public static String addBoldMarkers(int start, int end, String input) {
        String temp = input.substring(0, start) + BOLDMARKER.pattern +
                input.substring(start, end) + BOLDMARKER.pattern +
                input.substring(end);
        // Ensures there's no attempt to bold things twice.
        return temp.replaceAll("(" + BOLDMARKER.pattern + ")+", BOLDMARKER.pattern);
    }
}
