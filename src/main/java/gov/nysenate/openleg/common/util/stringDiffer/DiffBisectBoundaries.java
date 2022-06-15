package gov.nysenate.openleg.common.util.stringDiffer;

// Offsets for start and end of k loop.
// Prevents mapping of space beyond the grid.
public class DiffBisectBoundaries {
    private int start = 0;
    private int end = 0;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    // Ran off the {bottom|top} of the graph.
    public void incrementStart() {
        start += 2;
    }

    // Ran off the {right|left} of the graph.
    public void incrementEnd() {
        end += 2;
    }
}
