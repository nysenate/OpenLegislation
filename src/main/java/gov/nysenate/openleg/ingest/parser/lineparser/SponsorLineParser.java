package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.Person;

public class SponsorLineParser implements LineParser {
	private String sponsor = null;
	
	public void parseLineData(String line, String lineData, BillParser billParser) {
		if (lineData.charAt(0) != '0') {							
			sponsor = lineData.trim();
			if(sponsor.equals("DELETE"))
				sponsor = null;
		}
	}

	public void saveData(Bill bill) {
		if(sponsor != null) {
			bill.setSponsor(new Person(sponsor));
		}
	}

	public void clear() {
		sponsor = null;
	}
}
