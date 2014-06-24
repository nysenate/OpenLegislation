package gov.nysenate.openleg.scripts.admin;

import gov.nysenate.openleg.model.admin.Report;
import gov.nysenate.openleg.model.admin.ReportObservation;
import gov.nysenate.openleg.model.admin.SpotCheckBill;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.entity.Person;
import gov.nysenate.openleg.scripts.BaseScript;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.Storage;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotCheck extends BaseScript
{
    public static Logger logger = Logger.getLogger(SpotCheck.class);

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

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

        List<ReportObservation> observations = new ArrayList<ReportObservation>();
        HashMap<String, Integer> errorTotals = new HashMap<String, Integer>();
        for (String error_type : new String[] {"title", "summary", "sponsor", "cosponsors", "events", "pages"}) {
            errorTotals.put(error_type, 0);
        }

        String prefix = args[1];
        Date date = dateFormat.parse(prefix);
        logger.info("Processing daybreak files for: "+date);
        File directory = new File(args[0]);
        HashMap<String, SpotCheckBill> spotCheckBills = new HashMap<String, SpotCheckBill>();
        spotCheckBills.putAll(readDaybreak(new File(directory, prefix+".senate.low.html")));
        spotCheckBills.putAll(readDaybreak(new File(directory, prefix+".senate.high.html")));
        spotCheckBills.putAll(readDaybreak(new File(directory, prefix+".assembly.low.html")));
        spotCheckBills.putAll(readDaybreak(new File(directory, prefix+".assembly.high.html")));
        loadPageFile(new File(directory, prefix+".page_file.txt"), spotCheckBills);

        runner.update("insert ignore into report(time) values(?)", date);
        Report report = runner.query("select * from report where time = ?", new BeanHandler<Report>(Report.class), date);
        runner.update("delete from report_observation where reportId = ?", report.getId());

        for(String printNo : spotCheckBills.keySet()) {
            String billAmendment = "";
            String billNo = printNo+"-2013";
            if (printNo.matches(".*[A-Z]$")) {
                billAmendment = printNo.substring(printNo.length()-1);
                billNo = printNo.substring(0, printNo.length()-1)+"-2013";
            }

            Bill bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);

            if (bill == null) {
                logger.error("Missing bill 2013/bill/"+billNo);
                continue;
            }

            if (!bill.hasAmendment(billAmendment)) {
                logger.error("Missing bill amendment: "+billNo);
            }

            if (!bill.isPublished()) {
                logger.error("Bill Unpublished: "+billNo);
                continue;
            }

            // Compare the titles, ignore white space differences
            String jsonTitle = unescapeHTML(bill.getTitle());
            String lbdcTitle = spotCheckBills.get(printNo).getTitle();
            if (!lbdcTitle.isEmpty() && !stringEquals(jsonTitle, lbdcTitle, true, true)) {
                // What is this D?
                if (!printNo.startsWith("D")) {
                    logger.error("Title: "+billNo);
                    logger.error("  LBDC: "+lbdcTitle);
                    logger.error("  JSON: "+jsonTitle);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_TITLE", lbdcTitle, jsonTitle));
                    errorTotals.put("title", errorTotals.get("title")+1);
                }
            }

            // Compare the summaries. LBDC reports summary and law changes together
            String jsonLaw = bill.getLaw();
            String jsonSummary = unescapeHTML(bill.getSummary());
            String lbdcSummary = spotCheckBills.get(printNo).getSummary().replaceAll("\\s+", " ");

            if( jsonLaw != null && jsonLaw != "" && jsonLaw != "null") {
                jsonSummary = unescapeHTML(jsonLaw)+" "+jsonSummary;
            }

            if (lbdcSummary.equals("BILL SUMMARY NOT FOUND")) {
                lbdcSummary = "";
            }

            jsonSummary = jsonSummary.replace('§', 'S').replace('¶', 'P');
            if (!lbdcSummary.isEmpty() && !jsonSummary.replace(" ","").equals(lbdcSummary.replace(" ", "")) ) {
                if (!printNo.startsWith("D")) {
                    logger.error("Summary: "+printNo);
                    logger.error("  LBDC: "+lbdcSummary);
                    logger.error("  JSON: "+jsonSummary);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_SUMMARY", lbdcSummary, jsonSummary));
                    errorTotals.put("summary", errorTotals.get("summary")+1);
                }
            }

            String jsonSponsor = "";
            if (bill.getSponsor() != null) {
                jsonSponsor = unescapeHTML(bill.getSponsor().getFullname()).toUpperCase().replace(" (MS)","").replace("BILL", "").replace("COM", "");
            }
            String lbdcSponsor = spotCheckBills.get(printNo).getSponsor().toUpperCase().replace("BILL", "").replace("COM", "");
            if (!lbdcSponsor.isEmpty() && !jsonSponsor.replace(" ","").equals(lbdcSponsor.replace(" ", "")) ) {
                if (!printNo.startsWith("D")) {
                    logger.error("Sponsor: "+printNo);
                    logger.error("  LBDC: "+lbdcSponsor);
                    logger.error("  JSON: "+jsonSponsor);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_SPONSOR", lbdcSponsor, jsonSponsor));
                    errorTotals.put("sponsor", errorTotals.get("sponsor")+1);
                }
            }


            TreeSet<String> lbdcCosponsors = new TreeSet<String>(spotCheckBills.get(printNo).getCosponsors());
            TreeSet<String> jsonCosponsors = new TreeSet<String>();
            if ( bill.getAmendment(billAmendment).getCoSponsors() != null ) {
                List<Person> cosponsors = bill.getAmendment(billAmendment).getCoSponsors();
                for(Person cosponsor : cosponsors) {
                    jsonCosponsors.add(cosponsor.getFullname().toUpperCase());
                }
            }

            if (!lbdcCosponsors.isEmpty() && (lbdcCosponsors.size() != jsonCosponsors.size() || (!lbdcCosponsors.isEmpty() && !lbdcCosponsors.containsAll(jsonCosponsors))) ) {
                if (!printNo.startsWith("D")) {
                    logger.error("Cosponsors: "+printNo);
                    logger.error("  LBDC: "+lbdcCosponsors);
                    logger.error("  JSON: "+jsonCosponsors);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_COSPONSOR", StringUtils.join(lbdcCosponsors, " "), StringUtils.join(jsonCosponsors, " ")));
                    errorTotals.put("cosponsors", errorTotals.get("cosponsors")+1);
                }
            }

            ArrayList<String> lbdcEvents = spotCheckBills.get(printNo).getActions();
            ArrayList<String> jsonEvents = new ArrayList<String>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

            for (BillAction action : bill.getActions()) {
                jsonEvents.add(dateFormat.format(action.getDate())+" "+action.getText());
            }

            if (!lbdcEvents.isEmpty() &&  (lbdcEvents.size() != jsonEvents.size() || (!lbdcEvents.isEmpty() && !lbdcEvents.containsAll(jsonEvents))) ) {
                boolean substituted = StringUtils.join(jsonEvents, " ").toLowerCase().contains(" substituted ");
                boolean delivered = jsonEvents.get(jsonEvents.size()-1).toLowerCase().contains(" delivered to ");
                if (!printNo.startsWith("D") && !substituted && !delivered) {
                    logger.error("Events: "+printNo);
                    logger.error("  LBDC: "+lbdcEvents);
                    logger.error("  JSON: "+jsonEvents);
                    observations.add(new ReportObservation(report.getId(), billNo, "BILL_ACTION", StringUtils.join(lbdcEvents,"\n"), StringUtils.join(jsonEvents,"\n")));
                    errorTotals.put("events", errorTotals.get("events")+1);
                }
            }

            int lbdcPages = spotCheckBills.get(printNo).pages;
            int jsonPages = 0;
            Pattern pagePattern = Pattern.compile("(^\\s+\\w\\.\\s\\d+(--\\w)?\\s+\\d*(\\s+\\w\\.\\s\\d+(--\\w)?)?$|^\\s+\\d+\\s+\\d+\\-\\d+\\-\\d$|^\\s{11,}\\d{1,4}(--\\w)?$)");
            for (String line : bill.getAmendment(billAmendment).getFulltext().split("\n")) {
                if (pagePattern.matcher(line).find()) {
                    // logger.info(billNo+": "+line);
                    jsonPages++;
                }
            }

            if (jsonPages != lbdcPages) {
                logger.error("Pages: "+printNo);
                logger.error("  LBDC: "+lbdcPages);
                logger.error("  JSON: "+jsonPages);
                observations.add(new ReportObservation(report.getId(), billNo, "BILL_TEXT_PAGE", String.valueOf(lbdcPages), String.valueOf(jsonPages)));
                errorTotals.put("pages", errorTotals.get("pages")+1);
            }
        }

        for (ReportObservation observation : observations) {
            runner.update(
                "INSERT INTO report_observation (reportId, oid, field, actualValue, observedValue) VALUES (?, ?, ?, ?, ?)",
                observation.getReportId(),
                observation.getOid(),
                observation.getField(),
                observation.getActualValue(),
                observation.getObservedValue()
            );
        }

        System.out.println(errorTotals);
        System.out.println(spotCheckBills.keySet().size());
        System.exit(0);

        int total = 0;
        for(SpotCheckBill bill : spotCheckBills.values()) {
            total += 1+bill.amendments.size();
        }
        System.out.println("Estimated Total: "+total);

        System.out.println(spotCheckBills.size());
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
                if (bills.containsKey(sen_id)) {
                    bills.get(sen_id).pages = pages;
                }
                else {
                    // logger.error("Unknown bill '"+sen_id+"'");
                    SpotCheckBill bill = new SpotCheckBill();
                    bill.id = sen_id;
                    bill.pages = pages;
                    bills.put(sen_id, bill);
                }
            }

            if(!asm_id.isEmpty()) {
                if (bills.containsKey(asm_id)) {
                    bills.get(asm_id).pages = pages;
                }
                else {
                    //logger.error("Unknown bill '"+asm_id+"'");
                    SpotCheckBill bill = new SpotCheckBill();
                    bill.id = asm_id;
                    bill.pages = pages;
                    bills.put(asm_id, bill);
                }
            }

            if (!sen_id.isEmpty() && !asm_id.isEmpty()) {
                bills.get(sen_id).sameas = asm_id;
                bills.get(asm_id).sameas = sen_id;
            }
        }
    }

    public Pattern id = Pattern.compile("([A-Z]\\d+)([A-Z])");
    public Pattern row = Pattern.compile("<tr.*?>(.+?)</tr>");
    public Pattern stripParts = Pattern.compile(
                "<b>(.*?)</b>|"+                    // Remove bold text
                "<(a|/a|td).*?>|"+                  // Remove a, /a, and td tags. Leave /td for later
                "<br>\\s*Criminal Sanction Impact." // Remove criminal impact text if present
        );

    public HashMap<String, SpotCheckBill> readDaybreak(File dataFile) throws IOException
    {
        HashMap<String,SpotCheckBill> bills = new HashMap<String,SpotCheckBill>();

        // Open the daybreak file and remove new lines for the regular expressions
        String daybreak = FileUtils.readFileToString(dataFile, "latin1").replace("\r\n", " ");

        Matcher rowMatcher = row.matcher(daybreak);
        rowMatcher.find(); // Throw the first two rows away
        rowMatcher.find(); // They are just headers for the table
        while(rowMatcher.find()) {
            // Each table row corresponds to a single bill
            SpotCheckBill bill = new SpotCheckBill();
            String row = rowMatcher.group(1);

            String parts[] = stripParts.matcher(row)	// Match all non <br> and </td> tags
                    .replaceAll("")				// Remove them
                    .replace("</td>", "<br>")	// convert </td> to <br> as a special case
                    .split("<br>");				// Split the delimited row into parts


            bill.id = parts[0].trim();
            bill.setTitle(parts[2].trim());
            bill.law = parts[3].trim();
            bill.setSummary(parts[4].trim());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            for( int i=5; i< parts.length; i++ ) {
                String event = parts[i].trim();
                try {
                    dateFormat.parse(event.split(" ")[0]);
                    bill.getActions().add(event);
                } catch (ParseException e) {
                    //pass
                }
            }

            if (bill.id.startsWith("A")) {
                parts[1] = parts[1].replaceAll("([A-Z])\\.[� ]([A-Z'-]+)", "$2 $1");
                String[] all_sponsors = parts[1].split("; M-S:");
                String[] sponsors = all_sponsors[0].split(",");

                String sponsor = sponsors[0].trim();
                if (sponsor.contains("LOPEZ")) {
                    sponsor = "LOPEZ P";
                }
                bill.setSponsor(sponsor);
                for(int i=1; i<sponsors.length; i++) {
                    String coSponsor = sponsors[i].trim();
                    if (coSponsor.contains("LOPEZ")) {
                        coSponsor = "LOPEZ P";
                    }
                    bill.getCosponsors().add(coSponsor.trim());
                }

                if(all_sponsors.length == 2) {
                    for(String multisponsor : all_sponsors[1].split(",")) {
                        if (multisponsor.contains("LOPEZ")) {
                            multisponsor = "LOPEZ P";
                        }
                        bill.multisponsors.add(multisponsor.trim());
                    }
                }
            } else {
                String[] sponsors = parts[1].split("CO:");
                bill.setSponsor(sponsors[0].trim());
                if(sponsors.length == 2)
                    for(String cosponsor : sponsors[1].split(","))
                        bill.getCosponsors().add(cosponsor.trim());
            }

            if (bills.get(bill.id) != null) {
                System.out.println(bill.id+bill.amendments);
            } else {
                bills.put(bill.id, bill);
            }
        }

        return bills;
    }

}
