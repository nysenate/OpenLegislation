package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.bill.Bill;

public class ActClauseLineParser implements LineParser {
	private StringBuffer actClauseBuffer = new StringBuffer();
	
	public void parseLineData(String line, String lineData, BillParser billParser) {
		actClauseBuffer.append(lineData);
		actClauseBuffer.append(' ');
	}

	public void saveData(Bill bill) {
		if(actClauseBuffer.length() > 0) {
			bill.setActClause(actClauseBuffer.toString().trim());
		}
	}

	public void clear() {
		actClauseBuffer.setLength(0);
	}
}
