package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.bill.Bill;

public class LawSectionLineParser implements LineParser {
	private String lawSection = null;
	
	public void parseLineData(String line, String lineData, BillParser billParser) {
		lawSection = lineData.trim();
	}

	public void saveData(Bill bill) {
		bill.setLawSection(lawSection.trim());
	}

	public void clear() {
		lawSection = null;
	}
}
