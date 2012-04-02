package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

public class LawSectionLineParser implements LineParser {
    private String lawSection = null;

    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        lawSection = lineData.trim();
    }

    @Override
    public void saveData(Bill bill) {
        bill.setLawSection(lawSection.trim());
    }

    @Override
    public void clear() {
        lawSection = null;
    }
}
