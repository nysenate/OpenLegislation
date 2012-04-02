package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemoLineParser implements LineParser {
    private static final Pattern SECTION = Pattern.compile("(\\xa7|\\xDF)");

    private final Matcher SECTION_MATCHER = SECTION.matcher("");

    private final StringBuffer memoBuffer = new StringBuffer();
    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {
        //TODO should be able to remove
        String lineCode = line.substring(11,17);
        line = line.substring(17);

        if (line.indexOf("*END*") ==-1 && !lineCode.equals("M00000"))	{

            line = BillParser.replace(SECTION_MATCHER, line, "&sect;");

            memoBuffer.append(line);
            memoBuffer.append('\n');
        }
    }

    @Override
    public void saveData(Bill bill) {
        if(memoBuffer.length() > 0) {
            bill.setMemo(memoBuffer.toString());
        }
    }

    @Override
    public void clear() {
        memoBuffer.setLength(0);
    }
}
