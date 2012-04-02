package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummaryLineParser implements LineParser {
    private static final Pattern APOSTROPHE = Pattern.compile("\\x27(\\W|\\s)");
    //don't be deceived, the CONTROL pattern below is not empty
    private static final Pattern CONTROL = Pattern.compile("›");

    private final Matcher APOSTROPHE_MATCHER = APOSTROPHE.matcher("");
    private final Matcher CONTROL_MATCHER = CONTROL.matcher("");

    private final StringBuffer summaryBuffer = new StringBuffer();
    private final StringBuffer tempSummaryBuffer = new StringBuffer();
    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        lineData = BillParser.replace(APOSTROPHE_MATCHER, lineData, "&apos;$1");
        lineData = BillParser.replace(CONTROL_MATCHER, lineData, "S");

        /*
         * there is a recurring problem with summaries being
         * duplicated in the same activity stream, this attempts to avoid
         * appending a duplicated summary
         * 
         */
        if(summaryBuffer.toString().trim().contains(lineData)) {

            tempSummaryBuffer.append(lineData);
            tempSummaryBuffer.append(" ");

            if(summaryBuffer.equals(tempSummaryBuffer)) {
                tempSummaryBuffer.setLength(0);
            }
        }
        else {
            /*
             * the logic for bill summaries tries to not add duplicate lines,
             * but occasionally there ARE duplicate lines in real summaries
             */
            if(tempSummaryBuffer.length() > 0) {
                summaryBuffer.append(tempSummaryBuffer);
                tempSummaryBuffer.setLength(0);
            }

            summaryBuffer.append(lineData);
            summaryBuffer.append(" ");
        }
    }

    @Override
    public void saveData(Bill bill) {
        if(tempSummaryBuffer.length() > 0) {
            String temp = tempSummaryBuffer.toString();
            if(!temp.contains(summaryBuffer)
                    && !tempSummaryBuffer.equals(summaryBuffer)) {
                summaryBuffer.append(temp);
            }
        }

        if(summaryBuffer.length() > 0) {
            bill.setSummary(summaryBuffer.toString().trim());
        }
    }

    @Override
    public void clear() {
        summaryBuffer.setLength(0);
        tempSummaryBuffer.setLength(0);
    }
}
