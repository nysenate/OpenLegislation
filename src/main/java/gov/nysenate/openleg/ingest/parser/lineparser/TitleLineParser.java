package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleLineParser implements LineParser {
    private static final Pattern APOSTROPHE = Pattern.compile("\\x27(\\W|\\s)");
    //don't be deceived, the CONTROL pattern below is not empty
    private static final Pattern CONTROL = Pattern.compile("›");

    private final Matcher APOSTROPHE_MATCHER = APOSTROPHE.matcher("");
    private final Matcher CONTROL_MATCHER = CONTROL.matcher("");

    private final StringBuffer titleBuffer = new StringBuffer();
    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        lineData = BillParser.replace(APOSTROPHE_MATCHER, lineData, "&apos;$1");
        lineData = BillParser.replace(CONTROL_MATCHER, lineData, "S");

        titleBuffer.append(lineData);
        titleBuffer.append(" ");
    }

    @Override
    public void saveData(Bill bill) {
        if(titleBuffer.length() > 0) {
            bill.setTitle(titleBuffer.toString().trim());
        }
    }

    @Override
    public void clear() {
        titleBuffer.setLength(0);
    }
}