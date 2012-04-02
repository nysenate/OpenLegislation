package gov.nysenate.openleg.ingest.parser;

import gov.nysenate.openleg.ingest.parser.lineparser.ActClauseLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.BillDataLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.BillEventLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.CoSponsorLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.LawLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.LawSectionLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.LineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.MemoLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.MultiSponsorLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.SameAsLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.SponsorLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.SummaryLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.TextLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.TitleLineParser;
import gov.nysenate.openleg.ingest.parser.lineparser.VoteLineParser;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.util.EasyReader;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class BillParser extends SenateParser<Bill> {
    public static final String SOBI_CHARSET = "ISO-8859-1";

    public static final int YEAR_START_POS = 0;
    public static final int YEAR_END_POS = 4;
    public static final int BILL_START_POS = 4;
    public static final int BILL_END_POS = 11;
    public static final int CODE_POS = 11;
    public static final int DATA_POS = 12;

    /*
     * matches lines that begin with {YEAR}{BILL NO}{LINE CODE}
     */
    public static final Pattern VALID_LINE = Pattern.compile("^(?i)\\d{4}[sajr]\\d{5}[ a-z]");
    /*
     * matches white space and leading zeroes from bill no.. ex S01234 becomes S1234
     */
    public static final Pattern BILL_TRIMMER = Pattern.compile("(?i)(^(\\s+)|\\s+$|(?<=[sajr])0+)");
    public static final Pattern FORM_FEED = Pattern.compile("\f");

    private static Logger logger = Logger.getLogger(BillParser.class);

    private final HashMap<String, String> LINE_BILL_LOOKUP_MAP = new HashMap<String, String>();

    private final Matcher validLineMatcher = VALID_LINE.matcher("");
    private final Matcher billTrimmerMatcher = BILL_TRIMMER.matcher("");
    private final Matcher formFeed = FORM_FEED.matcher("");


    private final HashMap<String, LineParser> lineParsers = new HashMap<String, LineParser>();

    private final ArrayList<LineParser> activeLineParsers = new ArrayList<LineParser>();

    private Bill currentBill = null;

    public BillParser() {
        super(BillParser.class);

        lineParsers.put("1", new BillDataLineParser());
        lineParsers.put("2", new LawSectionLineParser());
        lineParsers.put("3", new TitleLineParser());
        lineParsers.put("4", new BillEventLineParser());
        lineParsers.put("5", new SameAsLineParser());
        lineParsers.put("6", new SponsorLineParser());
        lineParsers.put("7", new CoSponsorLineParser());
        lineParsers.put("8", new MultiSponsorLineParser());

        lineParsers.put("A", new ActClauseLineParser());
        lineParsers.put("B", new LawLineParser());
        lineParsers.put("C", new SummaryLineParser());
        lineParsers.put("M", new MemoLineParser());
        lineParsers.put("R", new TextLineParser());
        lineParsers.put("T", new TextLineParser());
        lineParsers.put("V", new VoteLineParser());
    }

    @Override
    public void parse(File file) {
        EasyReader reader = new EasyReader(file, SOBI_CHARSET).open();

        String line = null;

        while((line = reader.readLine()) != null) {
            if(validLine(line)) {
                line = BillParser.replace(formFeed, line, " ");

                String lineCode = getLineCode(line);
                String lineData = getLineData(line);

                if(lineData.length() > 0) {
                    LineParser lineParser = lineParsers.get(lineCode);

                    if(lineParser != null) {
                        String billNo = getLineBillString(line);
                        int year = new Integer(line.substring(YEAR_START_POS,YEAR_END_POS));
                        //if we see a billData line we need to commit to refresh the buffers
                        setCurrentBill(billNo, year, lineParser instanceof BillDataLineParser);

                        try {
                            lineParser.parseLineData(line, lineData, this);

                            if(!activeLineParsers.contains(lineParser)) {
                                activeLineParsers.add(lineParser);
                            }
                        }
                        catch (Exception e) {
                            logger.error(e);
                        }

                    }
                }
            }
        }
        //flush data
        commitCurrentBill();
    }



    public Bill getCurrentBill() {
        return currentBill;
    }

    public void setCurrentBill(String billNo, int year, boolean forceCommit) {
        if(currentBill != null) {
            if(!forceCommit && billNo.equals(currentBill.getSenateBillNo())) {
                //already working on current bill
                return;
            }

            commitCurrentBill();
        }

        Bill bill = new Bill();
        bill.setSenateBillNo(billNo);
        bill.setYear(year);

        currentBill = bill;
    }

    /*
     * saveData from activeLineParsers and purge list
     */
    public void commitCurrentBill() {
        if(lineParsers.size() > 0 && currentBill != null) {
            for(LineParser lineParser:activeLineParsers) {
                lineParser.saveData(currentBill);
                lineParser.clear();
            }

            activeLineParsers.clear();

            int index = -1;

            if((index = newSenateObjects.indexOf(currentBill)) == -1) {
                newSenateObjects.add(currentBill);
            }
            else {
                //if bill has already been seen we need to merge
                newSenateObjects.get(index).merge(currentBill);
            }
        }

        currentBill = null;
    }

    /*
     * a valid line starts with the following segments
     * 		year						: 4 characters
     * 		bill type (a, j, r or s)	: 1 chracter
     * 		bill number					: 5 characters
     * 		bill amendment level		: 1 character
     * 
     * ex. 2011S01234A
     */
    public boolean validLine(String line) {
        validLineMatcher.reset(line);
        return validLineMatcher.find() && line.length() > 11;
    }

    public String getLineCode(String line) {
        return line.substring(CODE_POS,CODE_POS + 1);
    }

    public String getLineData(String line) {
        return line.substring(DATA_POS);
    }

    public String getLineBillString(String line) {
        String billSection = line.substring(YEAR_START_POS, BILL_END_POS);

        String cachedBill = null;
        if((cachedBill = LINE_BILL_LOOKUP_MAP.get(billSection)) != null) {
            return cachedBill;
        }

        String billNo = TextFormatter.append(
                cleanBillString(line.substring(BILL_START_POS,BILL_END_POS)),
                "-",
                line.substring(YEAR_START_POS,YEAR_END_POS)
                ).toUpperCase();

        LINE_BILL_LOOKUP_MAP.put(billSection, billNo);

        return billNo;
    }

    public String cleanBillString(String billString) {
        return replace(billTrimmerMatcher, billString, "");
    }

    /**
     * hand rolled string replace to work with pre compiled patterns and matchers
     * @param matcher
     * @param string
     * @param replace
     * @return
     */
    public static String replace(Matcher matcher, String string, String replace) {
        if(matcher == null || string == null || replace == null)
            return null;

        StringBuffer replacement = new StringBuffer();

        matcher.reset(string);

        while(matcher.find()) {
            matcher.appendReplacement(replacement, replace);
        }
        matcher.appendTail(replacement);

        return replacement.toString();
    }

}
