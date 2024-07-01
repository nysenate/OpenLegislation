package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfParser;

import java.util.List;

public enum DayType {
    LEGISLATIVE, SESSION;

    public static DayType from(String transcriptText) {
        int pageCount = new TranscriptPdfParser(transcriptText).getPages().size();
        if (pageCount > 3) {
            return SESSION;
        }
        if (pageCount == 3) {
            return LEGISLATIVE;
        }
        return null;
    }
}
