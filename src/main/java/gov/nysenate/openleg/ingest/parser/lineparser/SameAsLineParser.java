package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.Bill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SameAsLineParser implements LineParser {
    private static final Pattern CHARACTERS = Pattern.compile("[ \\-.;]");
    private static final Pattern FORWARD_SLASH = Pattern.compile("/");

    private final Matcher CHARACTERS_MATCHER = CHARACTERS.matcher("");
    private final Matcher FORWARD_SLASH_MATCHER = FORWARD_SLASH.matcher("");

    private String sameAs = null;
    @Override
    public void parseLineData(String line, String lineData, BillParser billParser) {

        if(lineData.contains("DELETE")) return;

        if (lineData.startsWith("Same as Uni. ")) {
            lineData = lineData.substring("Same as Uni. ".length());
            sameAs = lineData.trim();
        }
        else if (lineData.startsWith("Same as ")) {
            lineData = lineData.substring("Same as ".length());
            sameAs = lineData.trim();
        }

        if (sameAs != null) {
            sameAs = BillParser.replace(CHARACTERS_MATCHER, sameAs, "");
            sameAs = BillParser.replace(FORWARD_SLASH_MATCHER, sameAs, ",");
        }
    }

    @Override
    public void saveData(Bill bill) {
        if(sameAs != null) {
            bill.setSameAs(sameAs.trim());
        }
    }

    @Override
    public void clear() {
        sameAs = null;
    }
}
