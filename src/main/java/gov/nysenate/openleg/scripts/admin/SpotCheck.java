package gov.nysenate.openleg.scripts.admin;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.admin.Report;
import gov.nysenate.openleg.model.admin.ReportObservation;
import gov.nysenate.openleg.model.admin.SpotCheckBill;
import gov.nysenate.openleg.scripts.BaseScript;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.Storage;

import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.util.Config;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

public class SpotCheck extends BaseScript
{
    public static Logger logger = Logger.getLogger(SpotCheck.class);

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private String SESSION_YEAR = Integer.toString(SessionYear.getSessionYear());
    public Pattern spotcheckBillId = Pattern.compile("([A-Z])(\\d+)([A-Z]?)");

    public static void main(String[] args) throws Exception
    {
        new SpotCheck().run(args);
    }

    public String unescapeHTML(String text)
    {
        return StringEscapeUtils.unescapeHtml4(text).replace("&apos;", "'");
    }

    public boolean stringEquals(String a, String b, boolean ignoreCase, boolean normalizeSpaces)
    {
        if (normalizeSpaces) {
            a = a.replaceAll("\\s+", " ");
            b = b.replaceAll("\\s+", " ");
        }

        return (ignoreCase) ? a.equalsIgnoreCase(b) : a.equals(b);
    }

    public void execute(CommandLine opts) throws IOException, ParseException, SQLException
    {
        QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

        String[] args = opts.getArgs();
        Storage storage = Application.getStorage();

        Config config = Application.getConfig();

        List<ReportObservation> observations = new ArrayList<ReportObservation>();
        HashMap<String, Integer> errorTotals = new HashMap<String, Integer>();
        for (String error_type : new String[] {"title", "summary", "sponsor", "cosponsors", "events", "pages", "amendments"}) {
            errorTotals.put(error_type, 0);
        }

        String prefix = args[0];
        Date date = dateFormat.parse(prefix);
        logger.info("Processing daybreak files for: "+date);
        File directory = new File(config.getValue("checkmail.lrsFileDir"));
        HashMap<String, SpotCheckBill> bills = new HashMap<String, SpotCheckBill>();
        logger.info("Reading " + prefix+".senate.low.html");
        bills.putAll(readDaybreak(new File(directory, prefix+".senate.low.html")));
        logger.info("Reading " + prefix+".senate.high.html");
        bills.putAll(readDaybreak(new File(directory, prefix+".senate.high.html")));
        logger.info("Reading " + prefix+".assembly.low.html");
        bills.putAll(readDaybreak(new File(directory, prefix+".assembly.low.html")));
        logger.info("Reading " + prefix+".assembly.high.html");
        bills.putAll(readDaybreak(new File(directory, prefix+".assembly.high.html")));
        logger.info("Reading " + prefix+".page_file.txt");
        loadPageFile(new File(directory, prefix+".page_file.txt"), bills);

        SpotCheckBill testbill = bills.get("S1743A");

        runner.update("insert ignore into report(time) values(?)", date);
        Report report = runner.query("select * from report where time = ?", new BeanHandler<Report>(Report.class), date);
        runner.update("delete from report_observation where reportId = ?", report.getId());

        for(String id : bills.keySet()) {
            //logger.info("checking bill "+id);
            String billNo = id+"-" + SESSION_YEAR;
            Bill jsonBill = (Bill)storage.get(SESSION_YEAR + "/bill/"+billNo, Bill.class);

            if (jsonBill == null) {
                logger.error("Missing bill "+ SESSION_YEAR + "/bill/"+billNo);
                continue;
            }

            if (!jsonBill.isPublished()) {
                logger.error("Bill Unpublished: "+billNo);
                continue;
            }

            // Compare the titles, ignore white space differences
            String jsonTitle = unescapeHTML(jsonBill.getTitle());
            String lbdcTitle = bills.get(id).getTitle();
            if (!lbdcTitle.isEmpty() && !stringEquals(jsonTitle, lbdcTitle, true, true)) {
                // What is this D?
                if (!id.startsWith("D")) {
                    logger.error("Title: "+billNo);
                    logger.error("  LBDC: "+lbdcTitle);
                    logger.error("  JSON: "+jsonTitle);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_TITLE", lbdcTitle, jsonTitle));
                    errorTotals.put("title", errorTotals.get("title")+1);
                }
            }

            // Compare the summaries. LBDC reports summary and law changes together
            String jsonLaw = jsonBill.getLaw();
            String jsonSummary = unescapeHTML(jsonBill.getSummary());
            String lbdcSummary = bills.get(id).getSummary().replaceAll("\\s+", " ");

            if( jsonLaw != null && jsonLaw != "" && jsonLaw != "null") {
                jsonSummary = unescapeHTML(jsonLaw)+" "+jsonSummary;
            }

            if (lbdcSummary.equals("BILL SUMMARY NOT FOUND")) {
                lbdcSummary = "";
            }

            jsonSummary = jsonSummary.replace('§', 'S').replace('¶', 'P');
            if (!lbdcSummary.isEmpty() && !jsonSummary.replace(" ","").equals(lbdcSummary.replace(" ", "")) ) {
                if (!id.startsWith("D")) {
                    logger.error("Summary: "+billNo);
                    logger.error("  LBDC: "+lbdcSummary);
                    logger.error("  JSON: "+jsonSummary);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_SUMMARY", lbdcSummary, jsonSummary));
                    errorTotals.put("summary", errorTotals.get("summary")+1);
                }
            }

            String jsonSponsor = "";
            if (jsonBill.getSponsor() != null) {
                jsonSponsor = unescapeHTML(jsonBill.getSponsor().getFullname()).toUpperCase().replace(" (MS)","").replace("BILL", "").replace("COM", "");
            }
            String lbdcSponsor = bills.get(id).getSponsor().toUpperCase().replace("BILL", "").replace("COM", "");
            if (lbdcSponsor.startsWith("RULES ") && lbdcSponsor.contains("(") && !lbdcSponsor.contains(")")){
                lbdcSponsor = lbdcSponsor.concat(")");
            }
            if (!lbdcSponsor.isEmpty() && !jsonSponsor.replace(" ","").equals(lbdcSponsor.replace(" ", "")) ) {
                if (!id.startsWith("D")) {
                    logger.error("Sponsor: "+billNo);
                    logger.error("  LBDC: "+lbdcSponsor);
                    logger.error("  JSON: "+jsonSponsor);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_SPONSOR", lbdcSponsor, jsonSponsor));
                    errorTotals.put("sponsor", errorTotals.get("sponsor")+1);
                }
            }


            TreeSet<String> lbdcCosponsors = new TreeSet<String>();
            TreeSet<String> jsonCosponsors = new TreeSet<String>();
            if ( jsonBill.getCoSponsors() != null ) {
                List<Person> cosponsors = jsonBill.getCoSponsors();
                for(Person cosponsor : cosponsors) {
                    // store all names as Capitalized without () for comparison
                    jsonCosponsors.add(WordUtils.capitalizeFully(cosponsor.getFullname().replaceAll("[\\(\\)]+", "")));
                }
            }
            // Capitalize and remove () for lbdc too
            for(String cosponsor : bills.get(id).getCosponsors()){
                lbdcCosponsors.add(WordUtils.capitalizeFully(cosponsor.replaceAll("[\\(\\)]+", "")));
            }


            if (!lbdcCosponsors.isEmpty() && (lbdcCosponsors.size() != jsonCosponsors.size() || (!lbdcCosponsors.isEmpty() && !lbdcCosponsors.containsAll(jsonCosponsors))) ) {
                if (!id.startsWith("D")) {
                    logger.error("Cosponsors: "+billNo);
                    logger.error("  LBDC: "+lbdcCosponsors);
                    logger.error("  JSON: "+jsonCosponsors);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_COSPONSOR", StringUtils.join(lbdcCosponsors, " "), StringUtils.join(jsonCosponsors, " ")));
                    errorTotals.put("cosponsors", errorTotals.get("cosponsors")+1);
                }
            }

            ArrayList<String> lbdcEvents = bills.get(id).getActions();
            ArrayList<String> jsonEvents = new ArrayList<String>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

            for (Action action : jsonBill.getActions()) {
                jsonEvents.add(dateFormat.format(action.getDate())+" "+action.getText());
            }

            if (!lbdcEvents.isEmpty() &&  (lbdcEvents.size() != jsonEvents.size() || (!lbdcEvents.isEmpty() && !lbdcEvents.containsAll(jsonEvents))) ) {
                boolean substituted = StringUtils.join(jsonEvents, " ").toLowerCase().contains(" substituted ");
                boolean delivered =  !jsonEvents.isEmpty() ? jsonEvents.get(jsonEvents.size()-1).toLowerCase().contains(" delivered to ") : false;
                if (!id.startsWith("D") && !substituted && !delivered) {
                    logger.error("Events: "+billNo);
                    logger.error("  LBDC: "+lbdcEvents);
                    logger.error("  JSON: "+jsonEvents);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_ACTION", StringUtils.join(lbdcEvents,"\n"), StringUtils.join(jsonEvents,"\n")));
                    errorTotals.put("events", errorTotals.get("events")+1);
                }
            }

            int lbdcPages = bills.get(id).pages;
            int jsonPages = TextFormatter.pdfPrintablePages(jsonBill).size();
            if (jsonBill.getFulltext().equals("")) {
                jsonPages = 0;
            }

            if (jsonPages != lbdcPages) {
                logger.error("Pages: "+billNo);
                logger.error("  LBDC: "+lbdcPages);
                logger.error("  JSON: "+jsonPages);
                observations.add(new ReportObservation(report.getId(), billNo, "BILL_TEXT_PAGE", String.valueOf(lbdcPages), String.valueOf(jsonPages)));
                errorTotals.put("pages", errorTotals.get("pages")+1);
            }

            ArrayList<String> lbdcAmendments = bills.get(id).getAmendments();
            ArrayList<String> jsonAmendments = (ArrayList) jsonBill.getAmendments();

            // Bill.getAmendments() does not include Bill itself, SpotCheckBill.getAmendments() does.
            jsonAmendments.add(jsonBill.getBillId());

            if (!amendmentsEqual(lbdcAmendments, jsonAmendments)) {
                logger.error("Amendments: " + billNo);
                logger.error("This Bill amendment : " + jsonBill.getBillId());
                logger.error("  LBDC: " + lbdcAmendments);
                logger.error("  JSON: " + jsonAmendments);
                observations.add(new ReportObservation(report.getId(), billNo, "BILL_AMENDMENT", StringUtils.join(lbdcAmendments, "\n"), StringUtils.join(jsonAmendments, "\n")));
                errorTotals.put("amendments", errorTotals.get("amendments") + 1);
            }
            //logger.info("Bill "+id+" checked");
        }

        logger.info("Bills checked, writing observations to db...");

        for (ReportObservation observation : observations) {
            //logger.info("inserting observation  "+observation.getOid() + " " + observation.getField() + " " + observation.getActualValue() + " " + observation.getObservedValue());
            runner.update(
                    "INSERT INTO report_observation (reportId, oid, field, actualValue, observedValue) VALUES (?, ?, ?, ?, ?)",
                    observation.getReportId(),
                    observation.getOid(),
                    observation.getField(),
                    observation.getActualValue(),
                    observation.getObservedValue()
            );
            //logger.info("observation " + observation.getOid() + " inserted");
        }

        System.out.println(errorTotals);
        System.out.println(bills.keySet().size());
        //System.exit(0);

        int total = 0;
        for(SpotCheckBill bill : bills.values()) {
            total += 1+bill.amendments.size();
        }
        System.out.println("Estimated Total: "+total);

        System.out.println(bills.size());

    }

    private boolean amendmentsEqual(ArrayList<String> lbdcAmendments, ArrayList<String> jsonAmendments) {
        if (lbdcAmendments.size() == 0) {
            return true;
        }
        Collections.sort(lbdcAmendments);
        Collections.sort(jsonAmendments);
        return lbdcAmendments.equals(jsonAmendments);
    }

    public void loadPageFile(File dataFile, HashMap<String, SpotCheckBill> bills) throws IOException
    {
        List<String> entries = FileUtils.readLines(dataFile, "latin1");
        entries.remove(0); // Remove the header line
        System.out.println(entries.size());
        for(String entry : entries) {
            String[] parts = entry.split(",");
            String sen_id = (parts[1]+parts[2].replaceAll("^0*", "")+parts[3]).trim();
            String asm_id = (parts[4]+parts[5].replaceAll("^0*", "")+parts[6]).trim();
            int pages = Integer.parseInt(parts[8]);

            if(!sen_id.isEmpty()) {
                updateBillFromPageFile(bills, sen_id, pages);
            }

            if(!asm_id.isEmpty()) {
                updateBillFromPageFile(bills, asm_id, pages);
            }

            if (!sen_id.isEmpty() && !asm_id.isEmpty()) {
                bills.get(sen_id).sameas = asm_id;
                bills.get(asm_id).sameas = sen_id;
            }
        }
    }

    private void updateBillFromPageFile(HashMap<String, SpotCheckBill> bills, String bill_id, int pages){
        if (bills.containsKey(bill_id)) {
            bills.get(bill_id).pages = pages;
        }
        else {
            // Look for
            //logger.error("Unknown bill '"+asm_id+"'");
            SpotCheckBill bill = new SpotCheckBill();
            bill.id = bill_id;
            bill.pages = pages;
            bills.put(bill_id, bill);
        }
    }

    private void updateAmendmentsFromPageFile(HashMap<String, SpotCheckBill> bills, String billId, Matcher billMatcher){
        String baseBill = billMatcher.group(1).trim() + billMatcher.group(2).trim();
        char amendment = billMatcher.group(3).charAt(0);
        if(bills.containsKey(baseBill) &&
                bills.get(baseBill).isCurrentAmendment() && !bills.get(baseBill).amendments.contains(billId)){
            bills.get(baseBill).amendments.add(billId);
        }
        for (int i = amendment-1; i >= 'A'; i--) {
            String checkedBillId = baseBill + (char)i;
            if(bills.containsKey(checkedBillId) &&
                        bills.get(checkedBillId).isCurrentAmendment() &&
                        !bills.get(checkedBillId).amendments.contains(billId) ){
                    bills.get(checkedBillId).amendments.add(billId);
            }
        }
    }

    private HashMap<String, SpotCheckBill> readDaybreak(File daybreak) throws IOException {
        String html = FileUtils.readFileToString(daybreak, "latin1").replaceAll("\\r?\\n", " ");

        Pattern billDataPattern = Pattern.compile("<tr.*?>(.+?)</tr>");
        Pattern stripHtmlTags = Pattern.compile("<b>(.*?)</b>|<(a|/a|td).*?>|<br>\\s*Criminal Sanction Impact.");

        Matcher billDataMatcher = billDataPattern.matcher(html);
        billDataMatcher.find(); // Throw the first two rows away
        billDataMatcher.find(); // They are just headers for the table

        HashMap<String, SpotCheckBill> bills = new HashMap<String, SpotCheckBill>();
        while(billDataMatcher.find()) {
            String rawBillHtml = billDataMatcher.group(1);
            String billParts[] = stripHtmlTags.matcher(rawBillHtml).replaceAll("").replace("</td>", "<br>").split("<br>");

            SpotCheckBill bill = extractBill(billParts);
            bills.put(bill.id, bill);
        }

        return bills;
    }

    private SpotCheckBill extractBill(String[] billParts) {
        SpotCheckBill bill = new SpotCheckBill();

        bill.setCurrentAmendment(true);
        bill.id = billParts[0].trim();
        bill.setTitle(billParts[2].trim());
        bill.law = billParts[3].trim();
        bill.setSummary(billParts[4].trim());
        bill.setActions(extractBillActions(billParts));
        bill.setSponsor(extractSponsor(billParts[1]));
        bill.setAmendments(extractAmendments(bill));

        if (isSenateBill(bill)) {
            bill.setCosponsors(extractSenateCosponsors(billParts[1]));
        } else { // is assembly bill
            bill.setCosponsors(extractAssemblyCosponsors(billParts[1]));
            bill.setMultisponsors(extractMultisponsors(billParts[1]));
        }

        return bill;
    }

    private ArrayList<String> extractBillActions(String[] billParts) {
        ArrayList<String> actions = new ArrayList<String>();
        for (int i = 5; i < billParts.length; i++) {
            String action = billParts[i].trim();
            try {
                SimpleDateFormat actionDateFormat = new SimpleDateFormat("MM/dd/yy");
                // Valid actions must start with date
                actionDateFormat.parse(action.split(" ")[0]);
                actions.add(action);
            } catch (ParseException e) {
                // Skip this action
            }

        }
        return actions;
    }

    // Used in old code for parsing all types of assembly sponsors:
    // parts[1] = parts[1].replaceAll("([A-Z])\\.[¦ ]([A-Z'-]+)", "$2 $1");

    /**
     * Extract the sponsor from a text field containing sponsor, cosponsors, and multisponsors.
     * <pre>Examples:
     * Senate: "DIAZ CO: ESPAILLAT, SAMPSON". returns DIAZ.
     * Assembly: "MONTESANO, GOODELL, NOJAY; M-S: Barclay, Hawley, Oaks, Tenney, Thiele". returns MONTESANO.
     * Assembly: "RULES COM (Request of Farrell, Wright)". returns itself unchanged.
     * </pre>
     * @param billPart string containing all sponsor, cosponsor, and multisponsor information.
     * @return sponsor of bill.
     */
    private String extractSponsor(String billPart) {
        String sponsor = billPart.split("CO:|; M-S:")[0].split(",")[0].trim();
        return formatInitials(sponsor);
    }

    private ArrayList<String> extractAmendments(SpotCheckBill bill) {
        ArrayList<String> amendments = new ArrayList<String>();

        Matcher idMatcher = spotcheckBillId.matcher(bill.id);
        if(anAmendmentExists(idMatcher)) {
            String billNo = idMatcher.group(2).trim();
            String billId = idMatcher.group(1).trim() + billNo;
            char amendment = idMatcher.group(3).charAt(0);
            for (int i = amendment-1; i >= 'A'; i--) {
                amendments.add(billId + String.valueOf((char)i) + "-" + SESSION_YEAR);
            }
            // Also add the base bill
            amendments.add(billId + "-" + SESSION_YEAR);
        }

        // Add bill itself as an amendment.
        amendments.add(bill.id + "-" + SESSION_YEAR);

        return amendments;
    }

    private boolean anAmendmentExists(Matcher idMatcher) {
        return idMatcher.find() && !idMatcher.group(3).isEmpty();
    }

    /**
     * In senate html files, 'CO:' separates sponsor from cosponsors.
     * i.e. PERALTA CO: ADDABBO, ESPAILLAT
     */
    private ArrayList<String> extractSenateCosponsors(String billPart) {
        ArrayList<String> cosponsors = new ArrayList<String>();

        String[] split = billPart.split("CO:");
        if (split.length >= 2) {
            for (String cosponsor : split[1].split(",")) {
                cosponsor = cosponsor.trim();
                cosponsor = formatInitials(cosponsor);
                cosponsors.add(cosponsor);
            }
        }
        return cosponsors;
    }

    /**
     * In assembly html files, '; M-S:' separates sponsor and cosponsors from multisponsors
     * i.e. KAVANAGH, PEOPLES-STOKES, JAFFEE, ROBINSON; M-S: Arroyo, Brook-Krasny, Gottfried, Lifton
     */
    private ArrayList<String> extractAssemblyCosponsors(String billPart) {
        ArrayList<String> cosponsors = new ArrayList<String>();

        String[] sponsorAndCosponsors = billPart.split("; M-S:")[0].split(",");
        // The first element is the sponsor, skip him.
        for (int i = 1; i < sponsorAndCosponsors.length; i++) {
            String cosponsor = sponsorAndCosponsors[i].trim();
            cosponsor = formatInitials(cosponsor);
            cosponsors.add(cosponsor);
        }
        return cosponsors;
    }

    private ArrayList<String> extractMultisponsors(String billPart) {
        ArrayList<String> multisponsors = new ArrayList<String>();

        String[] split = billPart.split("; M-S:");
        if (split.length >= 2) {
            for (String multisponsor : split[1].split(",")) {
                multisponsor = multisponsor.trim();
                multisponsor = formatInitials(multisponsor);
                multisponsors.add(multisponsor);
            }
        }
        return multisponsors;
    }

    /**
     * Changes a name to its uniquely identifying value if not already in that form.
     * i.e. change 'P. Lopez' to 'LOPEZ P'
     * @param name Name to format if in incorrect format.
     * @return Name formatted to match the uniquely identifying name found in sobi files.
     */
    private String formatInitials(String name) {
        String[] parts = name.split("[^\\x00-\\x7F]+"); // Split on non ascii characters.
        if (parts.length > 1) {
            String firstInitial = parts[0].replace(".", "");
            String lastName = parts[1];
            name = lastName + " " + firstInitial;
            name = name.toUpperCase();
        }

        return name;
    }

    private boolean isSenateBill(SpotCheckBill bill) {
        return bill.id.startsWith("S");
    }
}
