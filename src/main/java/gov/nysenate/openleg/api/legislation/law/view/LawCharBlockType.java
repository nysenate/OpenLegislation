package gov.nysenate.openleg.api.legislation.law.view;

import java.util.regex.Matcher;
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

    private static final Pattern LAW_CHAR_BLOCK_PATTERN;
    static {
        StringBuilder b = new StringBuilder();
        for (LawCharBlockType block : LawCharBlockType.values())
            b.append("(").append(block.pattern).append(")|");
        // Removes dangling pipe character.
        LAW_CHAR_BLOCK_PATTERN = Pattern.compile(b.substring(0, b.length()-1));
    }

    /**
     * Used to hide Pattern.
     * @return a matcher used for obtaining the Types of character blocks.
     */
    public static Matcher getMatcher(String toMatch) {
        return LAW_CHAR_BLOCK_PATTERN.matcher(toMatch);
    }

    /**
     * Adds markers around a section of text to indicate it should be bold.
     * @param start of bolding.
     * @param end of bolding.
     * @param input to marked.
     * @return the marked String.
     */
    public static String addBoldMarkers(int start, int end, String input) {
        String temp = input.substring(0, start) + BOLDMARKER.pattern +
                input.substring(start, end) + BOLDMARKER.pattern +
                input.substring(end);
        // Ensures there's no attempt to bold things twice.
        return temp.replaceAll("(" + BOLDMARKER.pattern + ")+", BOLDMARKER.pattern);
    }

    /**
     * Figures out what Type a String match belongs to.
     * @param s a matched String.
     * @return the proper type.
     */
    protected static LawCharBlockType parseType(String s) {
        if (s.equals(BOLDMARKER.pattern))
            return BOLDMARKER;
        if (s.equals(NEWLINE.pattern))
            return NEWLINE;
        if (s.contains(" ") || s.contains("\t"))
            return SPACE;
        return ALPHANUM;
    }
}
