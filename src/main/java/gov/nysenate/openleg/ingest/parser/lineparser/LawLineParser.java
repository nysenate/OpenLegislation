package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LawLineParser implements LineParser {
    private static final Pattern SECTION = Pattern.compile("(›|\\xF5|•À)");
    private static final Pattern CONTROL = Pattern.compile("\\xBD");

    private final Matcher SECTION_MATCHER = SECTION.matcher("");
    private final Matcher CONTROL_MATCHER = CONTROL.matcher("");

    private final StringBuffer lawBuffer = new StringBuffer();
    private final StringBuffer tempLawBuffer = new StringBuffer();
    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        if(lineData.contains("DELETE")) return;

        lineData = BillParser.replace(SECTION_MATCHER, lineData, "S");
        lineData = BillParser.replace(CONTROL_MATCHER, lineData, "");

        if(lawBuffer.toString().trim().contains(lineData)) {
            tempLawBuffer.append(lineData);
            tempLawBuffer.append(" ");

            if(lawBuffer.equals(tempLawBuffer)) {
                tempLawBuffer.setLength(0);
            }
        }
        else {
            if(tempLawBuffer.length() > 0) {
                lawBuffer.append(tempLawBuffer);
                tempLawBuffer.setLength(0);
            }

            lawBuffer.append(lineData);
            lawBuffer.append(" ");
        }
    }

    @Override
    public void saveData(Bill bill) {
        if(lawBuffer.length() > 0) {
            bill.setLaw(lawBuffer.toString().trim());
        }
    }

    @Override
    public void clear() {
        lawBuffer.setLength(0);
        tempLawBuffer.setLength(0);
    }
}
