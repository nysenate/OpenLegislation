package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.Person;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MultiSponsorLineParser implements LineParser {
    private ArrayList<Person> multiSponsors = new ArrayList<Person>();

    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        if (lineData.charAt(0) != '0') {

            String multisponsor = lineData.trim();

            StringTokenizer st = new StringTokenizer(multisponsor,",");
            while(st.hasMoreTokens()) {
                Person multiSponsor = new Person(st.nextToken().trim());
                if(!multiSponsors.contains(multiSponsor)) {
                    multiSponsors.add(multiSponsor);
                }
            }
        }
    }

    @Override
    public void saveData(Bill bill) {
        bill.setMultiSponsors(multiSponsors);
    }

    @Override
    public void clear() {
        multiSponsors = new ArrayList<Person>();
    }
}
