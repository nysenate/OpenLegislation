package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;

public class SponsorLineParser implements LineParser {
    private String sponsor = null;

    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        if (lineData.charAt(0) != '0') {
            sponsor = lineData.trim();
            if(sponsor.equals("DELETE"))
                sponsor = null;
        }
    }

    @Override
    public void saveData(Bill bill) {
        if(sponsor != null) {
            bill.setSponsor(new Person(sponsor));
        }
    }

    @Override
    public void clear() {
        sponsor = null;
    }
}
