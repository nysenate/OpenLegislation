package gov.nysenate.openleg.api.legislation.law.view;

import org.elasticsearch.common.collect.Tuple;

public class CharBlockInfo {
    public static final CharBlockInfo EMPTY = new CharBlockInfo("", null);
    private final Tuple<String, CharBlockType> info;

    public CharBlockInfo(String match, CharBlockType type) {
        this.info = new Tuple<>(match, type);
    }

    public String text() {
        return info.v1();
    }

    public boolean isBoldMarker() {
        return info.v2() == CharBlockType.BOLD;
    }

    public boolean isNewline() {
        return info.v2() == CharBlockType.NEWLINE;
    }

    public boolean isAlphanum() {
        return info.v2() == CharBlockType.ALPHANUM;
    }

    public boolean isSpace() {
        return info.v2() == CharBlockType.SPACE;
    }
}
