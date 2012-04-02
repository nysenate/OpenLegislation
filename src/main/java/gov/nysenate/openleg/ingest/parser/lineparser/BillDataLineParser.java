package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;

public class BillDataLineParser implements LineParser {
    private String previousVersion = null;
    private String sponsor = null;

    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        if (lineData.charAt(0) != 0) {
            int zeroIdx = lineData.indexOf("0000");
            if (zeroIdx != -1) {
                sponsor = lineData.substring(0,zeroIdx).trim();

                if(sponsor.equals("DELETE")) {
                    sponsor = null;
                }
            }
        }
        else {
            lineData = lineData.replaceAll("\\p{Cntrl}","");
            if(lineData.indexOf("00000", 5) == -1) {
                String billId = lineData.substring(5,12);
                int year = Integer.parseInt(lineData.substring(12,16));
                year = (year % 2 == 0 ? (year-1):year);

                String billType = billId.substring(0,1);
                String billRev = billId.substring(billId.length()-1).trim();
                int billNumber = Integer.parseInt(billId.substring(1,billId.length()-1));

                billId = billType + billNumber + billRev + "-" + year;
                previousVersion = billId;
            }
        }
    }

    @Override
    public void saveData(Bill bill) {
        if(previousVersion != null) {
            bill.addPreviousVersion(previousVersion.trim());
        }
        if(sponsor != null) {
            bill.setSponsor(new Person(sponsor.trim()));
        }
    }
    @Override
    public void clear() {
        this.previousVersion = null;
        this.sponsor = null;
    }
}
