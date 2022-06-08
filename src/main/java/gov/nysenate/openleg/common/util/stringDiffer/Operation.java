package gov.nysenate.openleg.common.util.stringDiffer;

/**
 * The data structure representing a diff is a Linked list of Diff objects:
 * {Diff(Operation.DELETE, "Hello"), Diff(Operation.INSERT, "Goodbye"),
 *  Diff(Operation.EQUAL, " world.")}
 * which means: delete "Hello", add "Goodbye" and keep " world."
 */
public enum Operation {
    DELETE('-'), INSERT('+'), EQUAL('=');

    private final char c;

    Operation(char c) {
        this.c = c;
    }

    public char getCharacter() {
        return c;
    }
}
