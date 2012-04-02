package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

public interface LineParser {
    public void parseLineData(String line, String lineData, BillParser billParser);
    public void saveData(Bill bill);
    public void clear();
}
