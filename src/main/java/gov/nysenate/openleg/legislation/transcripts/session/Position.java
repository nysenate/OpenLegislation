package gov.nysenate.openleg.legislation.transcripts.session;

public record Position(int pageNumStart, int lineNumStart) implements Comparable<Position> {
    public Position {
        if (pageNumStart <= 0 || lineNumStart <= 0) {
            throw new IllegalArgumentException("Page and line number must be positive");
        }
    }

    @Override
    public int compareTo(Position other) {
        int pageCmp = Integer.compare(pageNumStart, other.pageNumStart);
        return pageCmp != 0 ? pageCmp : Integer.compare(lineNumStart, other.lineNumStart);
    }
}
