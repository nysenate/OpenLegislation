package gov.nysenate.openleg.api.legislation.law.view;

import java.util.regex.Pattern;

public enum CharBlockType {
    BOLD("~~~~"), ALPHANUM("[^\\s~]+"), SPACE("[ \t]+"), NEWLINE("\n");

    private final String pattern;

    CharBlockType(String pattern) {
        this.pattern = pattern;
    }

    public static final Pattern LAW_CHAR_BLOCK_PATTERN;
    static {
        StringBuilder b = new StringBuilder();
        for (CharBlockType block : CharBlockType.values())
            b.append("(?<").append(block.name()).append(">").append(block.pattern).append(")|");
        // Removes dangling pipe character.
        LAW_CHAR_BLOCK_PATTERN = Pattern.compile(b.substring(0, b.length()-1));
    }

    public static String addBoldMarkers(String input) {
        return BOLD.pattern + input + BOLD.pattern;
    }
}
