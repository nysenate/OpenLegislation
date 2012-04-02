package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

public class ActClauseLineParser implements LineParser {
    private final StringBuffer actClauseBuffer = new StringBuffer();

    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        actClauseBuffer.append(lineData);
        actClauseBuffer.append(' ');
    }

    @Override
    public void saveData(Bill bill) {
        if(actClauseBuffer.length() > 0) {
            bill.setActClause(actClauseBuffer.toString().trim());
        }
    }

    @Override
    public void clear() {
        actClauseBuffer.setLength(0);
    }
}
