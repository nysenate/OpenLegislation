package gov.nysenate.openleg.common.util.stringDiffer;

/**
 * A simple class that maintains boundaries for StringDiffer operations.
 */
public class DiffBisectBoundaries {
    private int start = 0;
    private int end = 0;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    // Ran off the bottom or top of the graph.
    public void incrementStart() {
        start += 2;
    }

    // Ran off the right or left of the graph.
    public void incrementEnd() {
        end += 2;
    }
}
