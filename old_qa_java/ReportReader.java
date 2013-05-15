package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.qa.model.CouchInstance;
import gov.nysenate.openleg.qa.model.CouchSupport;
import gov.nysenate.openleg.qa.model.FieldName;
import gov.nysenate.openleg.qa.model.LbdcFile;
import gov.nysenate.openleg.qa.model.LbdcFile.AssociatedFields;
import gov.nysenate.openleg.qa.model.NonMatchingField;
import gov.nysenate.openleg.qa.model.ProblemBill;
import gov.nysenate.openleg.util.SessionYear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.ektorp.http.StdHttpClient;

public class ReportReader extends CouchSupport {
    public static final String FILE_TYPE = "file-type";
    public static final String PATH_TO_FILE = "path-to-file";
    public static final String REPORT_MISSING_DATA = "report-missing-data";
    public static final String DUMP = "dump";
    public static final String RESET_COUCH = "reset-couch";
    public static final String HELP = "help";

    public static void main(String[] args) {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("ft", FILE_TYPE, true, "(bill_html|memo|paging)");
        options.addOption("f", PATH_TO_FILE, true, "path to file being parsed");
        options.addOption("m", REPORT_MISSING_DATA, false, "refresh report on missing data");
        options.addOption("d", DUMP, true, "dump missing data information to file");
        options.addOption("r", RESET_COUCH, false, "reset couchdb");
        options.addOption("h", HELP, false, "print this message");

        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption("-h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("posix", options );
            }
            else {
                ReportReader reader = new ReportReader();

                if(line.hasOption(RESET_COUCH)) {
                    CouchInstance instance = CouchInstance.getInstance(CouchSupport.DATABASE_NAME, true, new StdHttpClient.Builder().build());
                    instance.getDbInstance().deleteDatabase(CouchSupport.DATABASE_NAME);
                }
                else if(line.hasOption(FILE_TYPE) && line.hasOption(PATH_TO_FILE)) {
                    ReportType reportType = null;
                    String fileType = line.getOptionValue(FILE_TYPE);

                    if(fileType.equalsIgnoreCase("bill_html"))
                        reportType = ReportType.BILL_HTML;
                    else if(fileType.equalsIgnoreCase("memo"))
                        reportType = ReportType.MEMO;
                    else if(fileType.equalsIgnoreCase("paging"))
                        reportType = ReportType.PAGING;

                    if(reportType==null)
                        throw new org.apache.commons.cli.ParseException("invalid file type: " + fileType);

                    reader.processFile(line.getOptionValue(PATH_TO_FILE), reportType);
                }
                else if(line.hasOption(REPORT_MISSING_DATA)) {
                    reader.reportMissingData();
                }
                else if(line.hasOption(DUMP)) {
                    reader.dumpToFile(line.getOptionValue(DUMP));
                }
                else {
                    throw new org.apache.commons.cli.ParseException("use with -h for options");
                }
            }
        }
        catch( org.apache.commons.cli.ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }
    }

    /*
     * report files come in three flavors:
     * 		1) an html dump of actions, summary, sponsors, law section
     * 		2) file of bills that have memos
     * 		3) csv containing length of bill texts
     */
    public enum ReportType {
        BILL_HTML, MEMO, PAGING
    }

    private final Logger logger = Logger.getLogger(ReportReader.class);

    public void processFile(String fileName, ReportType reportType) {
        processFile(new File(fileName), reportType);
    }

    public void processFile(File file, ReportType reportType) {
        logger.info("Processing file: " + file.getAbsolutePath() + " of type " + reportType);

        LbdcFile lbdcFile = null;
        switch(reportType) {
        case BILL_HTML:
            lbdcFile = new LbdcFileHtml(file);
            break;
        case MEMO:
            lbdcFile = new LbdcFileMemo(file);
            break;
        case PAGING:
            lbdcFile = new LbdcFilePaging(file);
        }

        FieldName[] fieldNames = new FieldName[0];

        AssociatedFields associatedFields = lbdcFile.getClass().getAnnotation(AssociatedFields.class);
        if(associatedFields != null) {
            fieldNames = associatedFields.value();
        }

        ArrayList<ProblemBill> problemBills = lbdcFile.getProblemBills(fieldNames);

        logger.info("Found " + problemBills.size() + " problematic bills");

        pbr.createOrUpdateProblemBills(problemBills, true);
        pbr.deleteNonProblemBills();
        pbr.rankProblemBills();
    }

    public void reportMissingData() {
        try {
            refreshMissingData();
            pbr.deleteNonProblemBills();
            pbr.rankProblemBills();
        } catch (ParseException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void refreshMissingData() throws ParseException, IOException {
        logger.info("Refreshing missing data");

        List<ProblemBill> problemBillList = pbr.findByMissingFields();
        logger.info("Found " + problemBillList.size() + "bills already missing fields");

        ReportBuilder reportBuilder = new ReportBuilder();
        HashMap<String, ProblemBill> reportedBillMap =
                reportBuilder.getBillReportSet(SessionYear.getSessionYear() + "");
        logger.info("Found " + reportedBillMap.size() + " bills missing fields in the index");

        for(ProblemBill problemBill:problemBillList)
        {
            //if bill was in missing report but no longer clear missingFields
            if(reportedBillMap.get(problemBill.getOid()) == null)
            {
                problemBill.setMissingFields(null);
                instance.getConnector().addToBulkBuffer(problemBill);
            }
        }

        pbr.createOrUpdateProblemBills(reportedBillMap.values(), true);

        instance.getConnector().flushBulkBuffer();
        instance.getConnector().clearBulkBuffer();
    }

    public List<ProblemBill> getProblemBills() {
        return pbr.findProblemBillsByRank();
    }

    public void dumpToFile(String filePath) {
        if(filePath == null)
            throw new NullPointerException();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath)));

            List<ProblemBill> problemBills = pbr.getAll();
            StringBuffer missing;
            StringBuffer nonMatching;
            StringBuffer line;

            for(ProblemBill pb:problemBills) {
                line = new StringBuffer(pb.getOid());
                missing = new StringBuffer();
                nonMatching = new StringBuffer();

                if(pb.getMissingFields() != null) {
                    for(String field:pb.getMissingFields()) {
                        if(missing.length() == 0)
                            missing.append(field);
                        else {
                            missing.append(", ");
                            missing.append(field);
                        }
                    }
                }

                if(pb.getNonMatchingFields() != null) {
                    for(NonMatchingField nmf:pb.getNonMatchingFields().values()) {
                        nonMatching.append("\n\t\t")
                        .append(nmf.getField())
                        .append("\n\t\t\tLBDC: ")
                        .append(nmf.getLbdcField())
                        .append("\n\t\t\tOpenLeg: ")
                        .append(nmf.getOpenField());
                    }
                }

                if(missing.length() > 0) line.append("\n\tmissing: ").append(missing);
                if(nonMatching.length() > 0) line.append("\n\tnon matching: ").append(nonMatching);
                line.append("\n\n");

                bw.write(line.toString());
            }
            bw.close();

        } catch (IOException e) {
            System.err.println("Could not write to file " + filePath);
            logger.error(e);
        }
    }
}
