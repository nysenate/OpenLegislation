package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class TextLineParser implements LineParser {
    private static Logger logger = Logger.getLogger(TextLineParser.class);

    private static final Pattern SECTION = Pattern.compile("(\\xa7|\\xDF)");

    private final Matcher SECTION_MATCHER = SECTION.matcher("");

    private final StringBuffer textBuffer = new StringBuffer();
    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        //TODO if line == delete (see original file for bill text
        String lineCode = line.substring(11,17);
        String lineText = line.substring(17);

        if (!lineText.contains("*DELETE*") && lineText.indexOf("*END*") == -1 && !lineCode.equals("T00000") && !lineCode.equals("R00000"))	{
            lineText = BillParser.replace(SECTION_MATCHER, lineText, "&sect;");

            textBuffer.append(lineText);
            textBuffer.append('\n');
        }
    }

    @Override
    public void saveData(Bill bill) {
        if(textBuffer.length() > 0) {
            try {
                bill.setFulltext(new String(textBuffer.toString().getBytes("UTF-8"),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void clear() {
        textBuffer.setLength(0);
    }
}
