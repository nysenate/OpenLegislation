package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.SpotCheckBill;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

public class SpotCheck extends BaseScript {
    public static Logger logger = Logger.getLogger(SpotCheck.class);

    public static void main(String[] args) throws Exception {
        new SpotCheck().run(args);
    }

    public String unescapeHTML(String text) {
        return StringEscapeUtils.unescapeHtml4(text).replace("&apos;", "'");
    }

    public boolean stringEquals(String a, String b, boolean ignoreCase, boolean normalizeSpaces) {
        if (normalizeSpaces) {
            a = a.replaceAll("\\s+", " ");
            b = b.replaceAll("\\s+", " ");
        }

        return (ignoreCase) ? a.equalsIgnoreCase(b) : a.equals(b);
    }



    public void execute(CommandLine opts) throws IOException, ParseException
    {
        String[] args = opts.getArgs();
        Storage storage = new Storage("/data/openleg/lbdc_test/json");

        HashMap<String, Integer> errors = new HashMap<String, Integer>();
        for (String error_type : new String[] {"title", "summary", "sponsor", "cosponsors", "events", "pages"}) {
            errors.put(error_type, 0);
        }

        String prefix = args[1];
        Date date = new SimpleDateFormat("yyyyMMdd").parse(prefix);
        logger.info("Processing daybreak files for: "+date);
        File directory = new File(args[0]);
        HashMap<String, SpotCheckBill> bills = new HashMap<String, SpotCheckBill>();
        bills.putAll(readDaybreak(new File(directory, prefix+".senate.low.html")));
        bills.putAll(readDaybreak(new File(directory, prefix+".senate.high.html")));
        bills.putAll(readDaybreak(new File(directory, prefix+".assembly.low.html")));
        bills.putAll(readDaybreak(new File(directory, prefix+".assembly.high.html")));
        loadPageFile(new File(directory, prefix+".page_file.txt"), bills);

        for(String id : bills.keySet()) {
            String billNo = id+"-2013";
            Bill bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);

            // Compare the titles, ignore white space differences
            String jsonTitle = unescapeHTML(bill.getTitle());
            String lbdcTitle = bills.get(id).title;
            if (!lbdcTitle.isEmpty() && !stringEquals(jsonTitle, lbdcTitle, true, true)) {
                // What is this D?
                if (!id.startsWith("D")) {
                    logger.error("Title: "+billNo);
                    logger.error("  LBDC: "+lbdcTitle);
                    logger.error("  JSON: "+jsonTitle);
                    errors.put("title", errors.get("title")+1);
                }
            }

            // Compare the summaries. LBDC reports summary and law changes together
            String jsonLaw = bill.getLaw();
            String jsonSummary = unescapeHTML(bill.getSummary());
            String lbdcSummary = bills.get(id).summary.replaceAll("\\s+", " ");

            if( jsonLaw != null && jsonLaw != "" && jsonLaw != "null") {
                jsonSummary = unescapeHTML(jsonLaw)+" "+jsonSummary;
            }

            if (lbdcSummary.equals("BILL SUMMARY NOT FOUND")) {
                lbdcSummary = "";
            }


            // Hack around encoding issues
            jsonSummary = jsonSummary.replace("P", "S");
            lbdcSummary = lbdcSummary.replace("P", "S");

            if (!lbdcSummary.isEmpty() && !jsonSummary.replace(" ","").equals(lbdcSummary.replace(" ", "")) ) {
                if (!id.startsWith("D")) {
                    logger.error("Summary: "+billNo);
                    logger.error("  LBDC: "+lbdcSummary);
                    logger.error("  JSON: "+jsonSummary);
                    errors.put("summary", errors.get("summary")+1);
                }
            }

            String jsonSponsor = unescapeHTML(bill.getSponsor().getFullname()).toUpperCase().replace(" (MS)","").replace("BILL", "").replace("COM", "");
            String lbdcSponsor = bills.get(id).sponsor.toUpperCase().replace("BILL", "").replace("COM", "");
            if (!lbdcSponsor.isEmpty() && !jsonSponsor.replace(" ","").equals(lbdcSponsor.replace(" ", "")) ) {
                if (!id.startsWith("D")) {
                    logger.error("Sponsor: "+billNo);
                    logger.error("  LBDC: "+lbdcSponsor);
                    logger.error("  JSON: "+jsonSponsor);
                    errors.put("sponsor", errors.get("sponsor")+1);
                }
            }


            TreeSet<String> lbdcCosponsors = new TreeSet<String>(bills.get(id).cosponsors);
            TreeSet<String> jsonCosponsors = new TreeSet<String>();
            if ( bill.getCoSponsors() != null ) {
                List<Person> cosponsors = bill.getCoSponsors();
                for(Person cosponsor : cosponsors) {
                    jsonCosponsors.add(cosponsor.getFullname().toUpperCase());
                }
            }

            if (!lbdcCosponsors.isEmpty() && (lbdcCosponsors.size() != jsonCosponsors.size() || (!lbdcCosponsors.isEmpty() && !lbdcCosponsors.containsAll(jsonCosponsors))) ) {
                if (!id.startsWith("D")) {
                    logger.error("Cosponsors: "+billNo);
                    logger.error("  LBDC: "+lbdcCosponsors);
                    logger.error("  JSON: "+jsonCosponsors);
                    errors.put("cosponsors", errors.get("cosponsors")+1);
                }
            }

            ArrayList<String> lbdcEvents = bills.get(id).actions;
            ArrayList<String> jsonEvents = new ArrayList<String>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

            for (Action action : bill.getActions()) {
                jsonEvents.add(dateFormat.format(action.getDate())+" "+action.getText());
            }

            if (!lbdcEvents.isEmpty() &&  (lbdcEvents.size() != jsonEvents.size() || (!lbdcEvents.isEmpty() && !lbdcEvents.containsAll(jsonEvents))) ) {
                if (!id.startsWith("D")) {
                    logger.error("Events: "+billNo);
                    logger.error("  LBDC: "+lbdcEvents);
                    logger.error("  JSON: "+jsonEvents);
                    errors.put("events", errors.get("events")+1);
                }
            }

            int lbdcPages = bills.get(id).pages;
            int jsonPages = 0;
            Pattern pagePattern = Pattern.compile("(^\\s+\\w\\.\\s\\d+(--\\w)?\\s+\\d*(\\s+\\w\\.\\s\\d+(--\\w)?)?$|^\\s+\\d+\\s+\\d+\\-\\d+\\-\\d$|^\\s{11,}\\d{1,4}(--\\w)?$)");
            for (String line : bill.getFulltext().split("\n")) {
                if (pagePattern.matcher(line).find()) {
                    // logger.info(billNo+": "+line);
                    jsonPages++;
                }
            }

            if (jsonPages != lbdcPages) {
                logger.error("Pages: "+billNo);
                logger.error("  LBDC: "+lbdcPages);
                logger.error("  JSON: "+jsonPages);
                errors.put("pages", errors.get("pages")+1);
            }
        }

        System.out.println(errors);

        System.out.println(bills.keySet().size());
        System.exit(0);

        int total = 0;
        for(SpotCheckBill bill : bills.values()) {
            total += 1+bill.amendments.size();
        }
        System.out.println("Estimated Total: "+total);

        System.out.println(bills.size());
    }

    public void loadPageFile(File dataFile, HashMap<String, SpotCheckBill> bills) throws IOException {
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
                    logger.error("Unknown bill '"+sen_id+"'");
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

    public HashMap<String, SpotCheckBill> readDaybreak(File dataFile) throws IOException {
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

            /*
			Matcher idMatcher = id.matcher(bill.id);
			if( idMatcher.find() ) {
				bill.id = idMatcher.group(1);
				for(int i = 65; i <= idMatcher.group(2).charAt(0); i++){
					bill.amendments.add(String.valueOf((char)i));
				}
			}
             */

            bill.title = parts[2].trim();
            bill.law = parts[3].trim();
            bill.summary = parts[4].trim();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            for( int i=5; i< parts.length; i++ ) {
                String event = parts[i].trim();
                try {
                    dateFormat.parse(event.split(" ")[0]);
                    bill.actions.add(event);
                } catch (ParseException e) {
                    //pass
                }
            }

            if (bill.id.startsWith("A")) {
                parts[1] = parts[1].replaceAll("([A-Z])\\.[¦ ]([A-Z'-]+)", "$2 $1");
                String[] all_sponsors = parts[1].split("; M-S:");
                String[] sponsors = all_sponsors[0].split(",");

                bill.sponsor = sponsors[0].trim();
                for(int i=1; i<sponsors.length; i++) {
                    bill.cosponsors.add(sponsors[i].trim());
                }

                if(all_sponsors.length == 2)
                    for(String multisponsor : all_sponsors[1].split(","))
                        bill.multisponsors.add(multisponsor.trim());
            } else {
                String[] sponsors = parts[1].split("CO:");
                bill.sponsor = sponsors[0].trim();
                if(sponsors.length == 2)
                    for(String cosponsor : sponsors[1].split(","))
                        bill.cosponsors.add(cosponsor.trim());
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
