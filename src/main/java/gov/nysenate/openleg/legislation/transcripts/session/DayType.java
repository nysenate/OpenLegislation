package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfParser;

import java.util.List;

public enum DayType {
    LEGISLATIVE, SESSION;

    public static DayType from(String transcriptText) {
        List<List<String>> pages = new TranscriptPdfParser(transcriptText).getPages();
        if (pages.size() > 3) {
            return SESSION;
        }
        if (pages.size() == 3 && pages.get(2).stream().dropWhile(line -> line.matches("[\\s\\d]+"))
                .findFirst().orElse("").trim().startsWith("There being no further business")) {
            return LEGISLATIVE;
        }
        return null;
    }
}
