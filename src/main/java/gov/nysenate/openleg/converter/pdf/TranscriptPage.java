package gov.nysenate.openleg.converter.pdf;

import gov.nysenate.openleg.util.TranscriptLine;

import java.util.ArrayList;
import java.util.List;

public class TranscriptPage
{
    private int lineCount;
    private TranscriptLine transcriptNumber;
    private List<TranscriptLine> lines = new ArrayList<TranscriptLine>();

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public TranscriptLine getTranscriptNumber() {
        return transcriptNumber;
    }

    public void setTranscriptNumber(TranscriptLine transcriptNumber) {
        this.transcriptNumber = transcriptNumber;
    }

    public List<TranscriptLine> getLines() {
        return lines;
    }

    public void addLine(TranscriptLine line) {
        lines.add(line);
    }
}
