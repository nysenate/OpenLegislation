package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class SpotCheck {

    public static Pattern id = Pattern.compile("([A-Z]\\d+)([A-Z])");
    public static Pattern row = Pattern.compile("<tr.*?>(.+?)</tr>");
    public static Pattern stripParts = Pattern.compile(
            "<b>(.*?)</b>|"+					// Remove bold text
                    "<(a|/a|td).*?>|"+  				// Remove a, /a, and td tags. Leave /td for later
                    "<br>\\s*Criminal Sanction Impact." // Remove criminal impact text if present
            );

    public static class Bill {
        int year;
        int pages;

        String id;
        String law;
        String title;
        String sponsor;
        String summary;

        ArrayList<String> actions;
        ArrayList<String> cosponsors;
        ArrayList<String> multisponsors;
        ArrayList<String> amendments;

        public Bill() {
            pages = year = 0;
            id = sponsor = title = summary = law = "";
            cosponsors = new ArrayList<String>();
            multisponsors = new ArrayList<String>();
            actions = new ArrayList<String>();
            amendments = new ArrayList<String>();
        }
    }

    public static String unescapeHTML(String text) {
        return StringEscapeUtils.unescapeHtml(text).replace("&apos;", "'");
    }

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger(SpotCheck.class);
        HashMap<String, SpotCheck.Bill> bills = new HashMap<String, SpotCheck.Bill>();

        HashMap<String, Integer> errors = new HashMap<String, Integer>();
        errors.put("title", 0);
        errors.put("summary", 0);
        errors.put("sponsor", 0);
        errors.put("cosponsors", 0);
        errors.put("events", 0);

        bills.putAll(SpotCheck.readDaybreak(new File("lbdc/SenLow.htm")));
        bills.putAll(SpotCheck.readDaybreak(new File("lbdc/SenHigh.htm")));
        bills.putAll(SpotCheck.readDaybreak(new File("lbdc/AsmLow.htm")));
        bills.putAll(SpotCheck.readDaybreak(new File("lbdc/AsmHigh.htm")));

        Storage storage = new Storage("/home/test/json/");
        for(String id : bills.keySet()) {
            String billNo = id+"-2011";

            gov.nysenate.openleg.model.Bill bill = (gov.nysenate.openleg.model.Bill)storage.get("2011/bill/"+billNo, gov.nysenate.openleg.model.Bill.class);

            // Compare the daybreak file to json file
            String jsonTitle = unescapeHTML(bill.getTitle());
            String lbdcTitle = bills.get(id).title;
            if ( !jsonTitle.replace(" ", "").equals(lbdcTitle.replace(" ","")) ) {
                if (!id.startsWith("D")) {
                    //logger.error("Title: "+billNo);
                    //logger.error("  LBDC: "+lbdcTitle);
                    //logger.error("  JSON: "+jsonTitle);
                    errors.put("title", errors.get("title")+1);
                }
            }

            String jsonLaw = bill.getLaw();
            String jsonSummary = unescapeHTML(bill.getSummary());
            String lbdcSummary = bills.get(id).summary;

            if( jsonLaw != null && jsonLaw != "" && jsonLaw != "null") {
                jsonSummary = unescapeHTML(jsonLaw)+" "+jsonSummary;
            }

            if ( !jsonSummary.replace(" ","").equals(lbdcSummary.replace(" ", "")) ) {
                if (!id.startsWith("D")) {
                    //logger.error("Summary: "+billNo);
                    //logger.error("  LBDC: "+lbdcSummary);
                    //logger.error("  JSON: "+jsonSummary);
                    errors.put("summary", errors.get("summary")+1);
                }
            }

            String jsonSponsor = unescapeHTML(bill.getSponsor().getFullname()).toUpperCase().replace(" (MS)","").replace("BILL", "").replace("COM", "");
            String lbdcSponsor = bills.get(id).sponsor.toUpperCase().replace("BILL", "").replace("COM", "");
            if ( !jsonSponsor.replace(" ","").equals(lbdcSponsor.replace(" ", "")) ) {
                if (!id.startsWith("D")) {
                    //logger.error("Sponsor: "+billNo);
                    //logger.error("  LBDC: "+lbdcSponsor);
                    //logger.error("  JSON: "+jsonSponsor);
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

            if ( lbdcCosponsors.size() != jsonCosponsors.size() || (!lbdcCosponsors.isEmpty() && !lbdcCosponsors.containsAll(jsonCosponsors)) ) {
                if (!id.startsWith("D")) {
                    //logger.error("Cosponsors: "+billNo);
                    //logger.error("  LBDC: "+lbdcCosponsors);
                    //logger.error("  JSON: "+jsonCosponsors);
                    errors.put("cosponsors", errors.get("cosponsors")+1);
                }
            }

            ArrayList<String> lbdcEvents = bills.get(id).actions;
            ArrayList<String> jsonEvents = new ArrayList<String>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

            for (Action action : bill.getActions()) {
                jsonEvents.add(dateFormat.format(action.getDate())+" "+action.getText());
            }

            if ( lbdcEvents.size() != jsonEvents.size() || (!lbdcEvents.isEmpty() && !lbdcEvents.containsAll(jsonEvents)) ) {
                if (!id.startsWith("D")) {
                    logger.error("Events: "+billNo);
                    logger.error("  LBDC: "+lbdcEvents);
                    logger.error("  JSON: "+jsonEvents);
                    errors.put("events", errors.get("events")+1);
                }
            }
        }

        System.out.println(errors);

        System.out.println(bills.keySet().size());
        System.exit(0);

        int total = 0;
        for(SpotCheck.Bill bill : bills.values()) {
            total += 1+bill.amendments.size();
        }
        System.out.println("Estimated Total: "+total);
        SpotCheck.loadPageFile(new File("PageFile.csv"), bills);
        System.out.println(bills.size());
    }

    public static void loadPageFile(File dataFile, HashMap<String, SpotCheck.Bill> bills) throws IOException {
        List<String> entries = FileUtils.readLines(dataFile);
        entries.remove(0); // Remove the header line
        System.out.println(entries.size());
        for(String entry : entries) {
            String[] parts = entry.split(",");
            String sen_id = parts[1]+parts[2].replaceAll("^0*", "");
            String asm_id = parts[4]+parts[5].replaceAll("^0*", "");

            if( sen_id != "")
                bills.get(sen_id);
            if( asm_id != "")
                bills.get(asm_id);
        }
    }

    public static HashMap<String, SpotCheck.Bill> readDaybreak(File dataFile) throws IOException {
        HashMap<String,SpotCheck.Bill> bills = new HashMap<String,SpotCheck.Bill>();

        // Open the daybreak file and remove new lines for the regular expressions
        String daybreak = FileUtils.readFileToString(dataFile).replace("\r\n", " ");

        Matcher rowMatcher = row.matcher(daybreak);
        rowMatcher.find(); // Throw the first two rows away
        rowMatcher.find(); // They are just headers for the table
        while(rowMatcher.find()) {

            // Each table row corresponds to a single bill
            SpotCheck.Bill bill = new SpotCheck.Bill();
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
