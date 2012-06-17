package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class BillProcessor {

    private final Logger logger;
    public File sobiDirectory;

    public static GregorianCalendar textFooterResolveDate = new GregorianCalendar(2011, 3, 23);

    public static SimpleDateFormat eventDateFormat = new SimpleDateFormat("MM/dd/yy");
    public static SimpleDateFormat voteDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    public static Pattern votePattern = Pattern.compile("(.{4}) (.{1,15})");
    public static Pattern billEventPattern = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{2}) (.*)");

    public static Pattern sameAsPattern = Pattern.compile("Same as( Uni\\.)? ([A-Z] ?[0-9]{1,5}-?[A-Z]?)");
    public static Pattern sobiHeaderPattern = Pattern.compile("^((\\d{4})([A-Z]\\d{5})([ A-Z])([1-9ABCMRTV]))(.*)");
    public static Pattern voteHeaderPattern = Pattern.compile("Senate Vote    Bill: (.{18}) Date: (.{10}).*");
    public static Pattern billDataPattern = Pattern.compile("(.{20})([0-9]{5}[ A-Z])(.{33})([ A-Z][0-9]{5}[ `\\-A-Z0-9])(.{8}).*");
    public static Pattern textPattern = Pattern.compile("00000\\.SO DOC ([ASC]) ([0-9R/A-Z ]{13}) ([A-Z* ]{24}) ([A-Z ]{20}) ([0-9]{4}).*");

    public static Pattern committeeEventTextPattern = Pattern.compile("(REFERRED|COMMITTED|RECOMMIT) TO (.*)");
    public static Pattern floorEventTextPattern = Pattern.compile("(REPORT CAL|THIRD READING|RULES REPORT)");
    public static Pattern substituteEventTextPattern = Pattern.compile("SUBSTITUTED (FOR|BY) (.*)");

    public static class ParseError extends Exception {
        private static final long serialVersionUID = 276526760129242006L;

        public String data;

        public ParseError(String message, String data) {
            super(message);
            this.data = data;
        }

        @Override
        public String getMessage() {
            return super.getMessage()+": "+data;
        }
    }

    public BillProcessor() {
        this.logger = Logger.getLogger(this.getClass());
    }

    public Bill loadBill(Storage storage, String billId, String billYear, String billAmendment, String oldBlock, StringBuffer blockData, String fileName, int lineNum) throws ParseError {
        // This really shouldn't be this complicated, but it is
        // TODO: Because we don't get 2009 data in order there are probably
        //       some data issues created by the logic below.

        // Get the original bill
        String oldkey;
        String amdkey;
        String bucket = billYear+"/bill/";
        String key = billId+"-"+billYear;
        Bill bill = (Bill)storage.get(bucket+key, Bill.class);

        // If the bill wasn't found, make a new one
        if (bill == null) {
            bill = new Bill();
            bill.setYear(Integer.parseInt(billYear));
            bill.setSenateBillNo(key);
            if (!billAmendment.isEmpty()) {
                logger.error("Bill Amendment filed without initial bill in file "+fileName+":"+lineNum+" - "+oldBlock);
            }
        }

        if (!billAmendment.isEmpty()) {
            amdkey = billId+billAmendment+"-"+billYear;
            Bill amd = (Bill)storage.get(bucket+amdkey, Bill.class);

            if (amd == null) {
                if(blockData.toString().startsWith("DELETE")) {
                    //If it is just a delete block just skip making the amendment
                    //this happens quite frequently for some reason.
                    throw new ParseError("New amendment with DELETE command only. Skipping", oldBlock+blockData);
                }

                // get the previous version and copy it by reading from file
                if(bill.amendments.isEmpty()) {
                    // In case this is an amendment introduced without an original (mostly 2009 data)
                    // we need to make sure to set the base bill we just made for flushing below.
                    oldkey = bill.getSenateBillNo();
                    storage.set(bucket+oldkey, bill);

                } else {
                    oldkey = bill.amendments.get(bill.amendments.size()-1);
                }

                // Flush to make sure we load the most recent copy of the bill
                storage.flush();

                // now that we're synced with the file system, load from file for a new copy
                amd = (Bill)storage.get(bucket+oldkey , Bill.class, false);
                if (amd==null) {
                    throw new ParseError("Recorded amendment not found in storage", bucket+oldkey);
                }

                // Set the amendment to the correct bill number
                amd.setSenateBillNo(amdkey);

                // Update the all amendment lists and mark existing ones as inactive
                for(String tempKey : bill.amendments) {
                    storage.flush();
                    Bill temp = (Bill)storage.get(bucket+tempKey, Bill.class);
                    temp.setActive(false);
                    temp.amendments.add(amdkey);
                    storage.set(bucket+tempKey, temp);
                }

                // Update the base amendment list and mark it as inactive
                bill.amendments.add(amdkey);
                bill.setActive(false);
                storage.set(bucket+key, bill);
            }

            bill = amd;
            key = amdkey;

        } else if(!bill.amendments.isEmpty()) {
            amdkey = bill.amendments.get(bill.amendments.size()-1);
            Bill amd = (Bill)storage.get(bucket+amdkey, Bill.class);
            if (amd==null) {
                throw new ParseError("Recorded amendment not found on filesystem",amdkey);
            }

            bill = amd;
            key = amdkey;
        }

        return bill;
    }

    public void saveBill(Storage storage, Bill bill) throws ParseError {
        // Sync certain information across versions.
        // This is a hack around the horrible 2009 data set we were given
        // TODO: We should only need to do this for certain line codes
        String bucket = bill.getYear()+"/bill/";
        if (bill.getSponsor()!=null) {
            // Update the base bill if we are not the base bill
            String baseBill = bill.getSenateBillNo().replaceAll("([A-Z][0-9]+).?-([0-9]{4})","$1-$2");
            List<String> amendments = bill.amendments;
            if (!baseBill.equals(bill.getSenateBillNo())) {
                Bill base = (Bill)storage.get(bucket+baseBill, Bill.class);
                base.setSponsor(bill.getSponsor());
                storage.set(bucket+baseBill, base);
                amendments = base.amendments;
            }
            // Update amendments (starting from the base
            for(String amendment : amendments ) {
                if (!amendment.equals(bill.getSenateBillNo())) {
                    Bill amd = (Bill)storage.get(bucket+amendment, Bill.class);
                    amd.setSponsor(bill.getSponsor());
                    storage.set(bucket+amendment, amd);
                }
            }
        }
        storage.set(bucket+bill.getSenateBillNo(), bill);
    }

    public void process(File sobiFile, Storage storage) throws IOException {

        // Initialize file variables
        String oldBlock = "";
        String newBlock = "";
        StringBuffer blockData = new StringBuffer();
        String billYear = "";
        String billId = "";
        String billAmendment = "";
        String lineCode = "";
        String fileName = sobiFile.getName();

        Date date;
        try {
            date = BillProcessor.sobiDateFormat.parse(fileName);
        } catch (ParseException e) {
            logger.error("Unparseable date: "+fileName);
            return;
        }

        // Loop through the lines in the file
        logger.info("Reading file: "+fileName);
        List<String> lines = FileUtils.readLines(sobiFile);
        lines.add(""); // Add a line to remove last line edge case
        for(int lineNum=0; lineNum<lines.size(); lineNum++) {
            String line = lines.get(lineNum);

            // Check to see if the current line is in the SOBI format
            Matcher sobiHeader = sobiHeaderPattern.matcher(line);

            // Supply a default newBlock identifier for non-matching lines
            newBlock = sobiHeader.find() ? sobiHeader.group(1) : "";

            // If we previously had a block and the new block is different
            // commit the old block before starting a new one.
            if (!oldBlock.equals("") && !newBlock.equals(oldBlock)) {

                try {
                    Bill bill = loadBill(storage, billId, billYear, billAmendment, oldBlock, blockData, fileName, lineNum);

                    // commit block
                    try {
                        switch (lineCode.charAt(0)) {
                            case '1': applyBillData(blockData.toString(), bill, date); break;
                            case '2': applyLawSection(blockData.toString(), bill, date); break;
                            case '3': applyTitle(blockData.toString(), bill, date); break;
                            case '4': applyBillEvent(blockData.toString(), bill, date); break;
                            case '5': applySameAs(blockData.toString(), bill, date); break;
                            case '6': applySponsor(blockData.toString(), bill, date); break;
                            case '7': applyCosponsors(blockData.toString(), bill, date); break;
                            case '8': applyMultisponsors(blockData.toString(), bill, date); break;
                            case '9': applyProgramInfo(blockData.toString(), bill, date); break;
                            case 'A': applyActClause(blockData.toString(), bill, date); break;
                            case 'B': applyLaw(blockData.toString(), bill, date); break;
                            case 'C': applySummary(blockData.toString(), bill, date); break;
                            case 'M':
                            case 'R':
                            case 'T': applyText(blockData.toString(), bill, date); break;
                            case 'V': applyVoteMemo(blockData.toString(), bill, date); break;
                            default: throw new ParseError("Invalid Line Code", lineCode);
                        }

                        bill.addSobiReference(sobiFile.getName());
                        bill.setModified(date.getTime());
                        saveBill(storage, bill);
                    } catch (ParseError e) {
                        throw e;
                    } catch (Exception e) {
                        logger.error("Unexpected Exception", e);
                        throw new ParseError(e.getMessage(), blockData.toString());
                    }

                } catch (ParseError e) {
                    logger.error("ParseError at "+fileName+":"+lineNum, e);
                }

                // cleanup
                blockData = new StringBuffer();
            }

            // Move our identifier forward
            oldBlock = newBlock;

            // Skip lines that did not match the SOBI format
            if (newBlock.equals("")) continue;


            if (blockData.length()==0) {
                // If we're starting from a blank slate, initialize block values
                billYear = sobiHeader.group(2);
                billId = sobiHeader.group(3).replaceAll("(?<=[A-Z])0*", "");
                billAmendment = sobiHeader.group(4).trim();
                lineCode = sobiHeader.group(5);
                blockData.append(sobiHeader.group(6));

            } else {
                // Otherwise, build the data string, carry the new lines
                blockData.append("\r\n"+sobiHeader.group(6));
            }

            // Special case for the type 1 deletes!
            if (lineCode.equals("1") && sobiHeader.group(6).startsWith("DELETE")) {
                String bucket = billYear+"/bill/";
                String key = billId+billAmendment+"-"+String.valueOf(billYear);
                storage.del(bucket+key);

                if (!billAmendment.isEmpty()) {
                    // We need to remove all references to this amendment
                    String oldKey = billId+"-"+billYear;
                    Bill oldBill = (Bill)storage.get(bucket+oldKey, Bill.class);
                    oldBill.amendments.remove(key);
                    storage.set(bucket+oldKey, oldBill);
                    for (String ammendment : oldBill.amendments) {
                        Bill bill = (Bill)storage.get(bucket+ammendment, Bill.class);
                        bill.amendments.remove(key);
                        storage.set(bucket+ammendment, bill);
                    }
                }

                blockData = new StringBuffer();
                oldBlock = "";
            }
        }
    }


    public void applyBillEvent(String data, Bill bill, Date date) throws ParseError {
        // currently we don't want to keep track of assembly committees
        Boolean trackCommittees = !bill.getSenateBillNo().startsWith("A");

        ArrayList<Action> events = new ArrayList<Action>();
        String sameAs = "";
        Boolean stricken = false;
        String currentCommittee = "";
        List<String> pastCommittees = new ArrayList<String>();

        for (String line : data.split("\r\n")) {
            Matcher billEvent = billEventPattern.matcher(line);
            if (billEvent.find()) {
                try {
                    Date eventDate = eventDateFormat.parse(billEvent.group(1));
                    String eventText = billEvent.group(2).trim();

                    // Horrible hack - fixes instances where multiple identical events
                    // occur on the same day on the same bill. Increment the time by
                    // seconds until we get clear. This also allows for strict ordering
                    // by date. Assume events come in chronological order.
                    // TODO: fix this horrible hack somehow
                    // TODO: account for multiple action blocks for the same bill in a row
                    Calendar c = Calendar.getInstance();
                    c.setTime(eventDate);
                    for(Action event : events) {
                        if(event.getDate().equals(c.getTime())) {
                            c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 1);
                        }
                    }

                    events.add(new Action(bill, c.getTime(), eventText));

                    eventText = eventText.toUpperCase();

                    Matcher committeeEventText = committeeEventTextPattern.matcher(eventText);
                    Matcher substituteEventText = substituteEventTextPattern.matcher(eventText);
                    Matcher floorEventText = floorEventTextPattern.matcher(eventText);

                    if (eventText.contains("ENACTING CLAUSE STRICKEN")) {
                        stricken = true;
                    } else if (trackCommittees && committeeEventText.find()) {
                        if (!currentCommittee.isEmpty())
                            pastCommittees.add(currentCommittee);
                        currentCommittee = committeeEventText.group(2);
                    } else if (trackCommittees && floorEventText.find()){
                        if(!currentCommittee.isEmpty()) {
                            pastCommittees.add(currentCommittee);
                        }
                        currentCommittee = "";
                    } else if(substituteEventText.find()) {
                        String newSameAs = substituteEventText.group(2);
                        for(String billId : sameAs.split(",")) {
                            if (!billId.trim().isEmpty()) {
                                newSameAs += ", " + billId.trim();
                            }
                        }
                        sameAs = newSameAs;
                    }

                } catch (ParseException e) {
                    throw new ParseError("eventDateFormat parse failure", billEvent.group(1));
                }
            } else {
                throw new ParseError("billEventPattern not matched", line);
            }
        }

        bill.setActions(events);
        bill.setSameAs(sameAs);
        bill.setCurrentCommittee(currentCommittee);
        bill.setPastCommittees(pastCommittees);
        bill.setStricken(stricken);
    }

    public void applyText(String data, Bill bill, Date date) throws ParseError {
        // BillText and MemoText can be handled the same way
        String type = "";
        StringBuffer text = null;

        for (String line : data.split("\r\n")) {
            Matcher header = textPattern.matcher(line);
            if (line.startsWith("00000") && header.find()) {
                //TODO: This information should be used
                //TODO: If house == C then bills can be used for SAME AS
                //String house = header.group(1);
                //String bills = header.group(2).trim();
                String action = header.group(3).trim();
                type = header.group(4).trim();
                //String year = header.group(5);

                if (!type.equals("BTXT") && !type.equals("RESO TEXT") && !type.equals("MTXT"))
                    throw new ParseError("Unknown text type found", type);

                if (action.equals("*DELETE*")) {
                    if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                        bill.setFulltext("");
                    } else if (type.equals("MTXT")) {
                        bill.setMemo("");
                    }

                } else if (action.equals("*END*")) {
                    if (text != null) {
                        if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                            bill.setFulltext(text.toString());
                        } else if (type.equals("MTXT")) {
                            bill.setMemo(text.toString());
                        }
                        text = null;
                    } else {
                        throw new ParseError("Text END Found before a body", line);
                    }
                } else if (action.equals("")) {
                    if (text == null) {
                        //First header for this text segment so initialize
                        text = new StringBuffer();
                        text.ensureCapacity(data.length());
                    } else {
                        //Every 100th line is a repeated header for some reason
                    }
                } else {
                    throw new ParseError("Unknown text action found", action);
                }
            } else if (text != null) {
                // Remove the leading numbers
                text.append(line.substring(5)+"\n");

            } else {
                throw new ParseError("Text Body found before header", line);
            }
        }

        if (text != null) {
            // This is a known issue that was resolved on 03/23/2011
            if (textFooterResolveDate.after(date)) {
                throw new ParseError("Finished text data without a footer", "");

            // Commit what we have and move on
            } else {
                if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                    bill.setFulltext(text.toString());
                } else if (type.equals("MTXT")) {
                    bill.setMemo(text.toString());
                }
            }
        }
    }

    public void applyProgramInfo(String data, Bill bill, Date date) throws ParseError {
        // This information currently isn't used for anything
        //if (!data.equals(""))
        //    throw new ParseError("Program info not implemented", data);
    }

    public void applyVoteMemo(String data, Bill bill, Date date) throws ParseError {
        // Example of a double vote entry: SOBI.D110119.T140802.TXT:390
        Vote vote = null;
        for(String line : data.split("\r\n")) {

            //Start over if we hit a header, sometimes we get back to back entries
            Matcher voteHeader = voteHeaderPattern.matcher(line);
            if (voteHeader.find()) {
                try {
                    // TODO: Parse out sequence number once LBDC includes it.
                    vote = new Vote(bill, voteDateFormat.parse(voteHeader.group(2)), Vote.VOTE_TYPE_FLOOR, "1");
                } catch (ParseException e) {
                    throw new ParseError("voteDateFormat not matched", line);
                }

            //Otherwise, build the existing vote
            } else if (vote!=null){
                Matcher voteLine = votePattern.matcher(line);
                while(voteLine.find()) {
                    String type = voteLine.group(1).trim();
                    Person voter = new Person(voteLine.group(2).trim());

                    if (type.equals("Aye")) {
                        vote.addAye(voter);
                    } else if (type.equals("Nay")) {
                        vote.addNay(voter);
                    } else if (type.equals("Abs")) {
                        vote.addAbsent(voter);
                    } else if (type.equals("Abd")) {
                        vote.addAbstain(voter);
                    } else if (type.equals("Exc")) {
                        vote.addExcused(voter);
                    } else {
                        throw new ParseError("Unknown vote type found", line);
                    }
                }

            } else {
                throw new ParseError("Hit vote data without a header", data);
            }
        }

        //Misnomer, will actually update the vote if it already exists
        if (vote!=null)
            bill.addVote(vote);
    }

    public void applyActClause(String data, Bill bill, Date date) throws ParseError {
        if (data.trim().startsWith("DELETE")) {
            bill.setActClause("");
        } else {
            bill.setActClause(data.replace("\r\n", " ").trim());
        }
    }

    public void applyLawSection(String data, Bill bill, Date date) throws ParseError {
        // No DELETE command for law section
        if (data.contains("\r\n"))
            throw new ParseError("LawSection (2) blocks should only ever be 1 line long",data);

        bill.setLawSection(data.trim());
    }

    public void applyBillData(String data, Bill bill, Date date) throws ParseError{
        // Get rid of the nulls first and foremost
        data = data.replace('\0', ' ');

        // Sequential lines should be executed in order
        for(String line : data.split("\r\n")) {

            Matcher billData = billDataPattern.matcher(line);
            if (billData.find()) {
                //TODO: Find a possible use for this information
                String sponsor = billData.group(1).trim();
                //String reprint = billData.group(2);
                //String blurb = billData.group(3);
                String oldbill = billData.group(4).trim().replaceAll("[0-9`-]$", "");
                //String lbdnum = billData.group(5);

                if (!sponsor.isEmpty()) {
                    bill.setSponsor(new Person(sponsor));
                }
                bill.addPreviousVersion(oldbill);
            } else {
                throw new ParseError("billDataPattern not matched", line);
            }
        }
    }

    public void applyLaw(String data, Bill bill, Date date) throws ParseError {
        if (data.trim().startsWith("DELETE")) {
            // The Law delete code should also remove the summary information
            bill.setLaw("");
            bill.setSummary("");

        } else {
            // We'll definitely need to clean this data up more than a little bit..
            // data = data.replaceAll("\\xBD", "");
            bill.setLaw(data.replace("\r\n", " ").replace("õ", "S").replace("ô","P").replace("ï¿½","S").replace((char)65533+"", "S").trim());
        }
    }

    public void applyCosponsors(String data, Bill bill, Date date) throws ParseError {
        // No DELETE code for coSponsors, sent through sponsor
        // instead which deletes the whole package

        // New values are always replacements of existing sets...
        ArrayList<Person> coSponsors = new ArrayList<Person>();
        if (!data.isEmpty()) {
            for(String coSponsor : data.replace("\r\n", " ").split(",")) {
                coSponsors.add(new Person(coSponsor.trim()));
            }
        }
        bill.setCoSponsors(coSponsors);
    }

    public void applyMultisponsors(String data, Bill bill, Date date) throws ParseError {
        // No DELETE code for multisponsors, sent through sponsor
        // instead which deletes the whole package

        // New values are always replacements of existing sets...

        ArrayList<Person> multiSponsors = new ArrayList<Person>();
        if (!data.isEmpty()) {
            for(String multiSponsor : data.replace("\r\n", " ").split(",")) {
                multiSponsors.add(new Person(multiSponsor.trim()));
            }
        }
        bill.setMultiSponsors(multiSponsors);
    }

    public void applySponsor(String data, Bill bill, Date date) throws ParseError{
        // Apply the lines in order given as each represents its own "block"
        for(String line : data.split("\r\n")) {
            if (line.startsWith("DELETE")) {
                // When we receive a delete for sponsor, remove ALL sponsor information
                bill.setSponsor(null);
                bill.setCoSponsors(new ArrayList<Person>());
                bill.setMultiSponsors(new ArrayList<Person>());

            } else {
                bill.setSponsor(new Person(line.trim()));
            }
        }
    }

    public void applySummary(String data, Bill bill, Date date) throws ParseError{
        // The DELETE code for the summary goes through the law block (B)
        // Combine the lines with a space and handle special character issues..
        // Again, I don't have any examples of these special characters right now
        // data = data.replace("","S").replaceAll("\\x27(\\W|\\s)", "&apos;$1");
        bill.setSummary(data.replace("\r\n", " ").trim());
    }

    public void applyTitle(String data, Bill bill, Date date) throws ParseError {
        // No DELETE code for titles
        // Combine the lines with a space and handle special character issues..
        // Again, I don't have any examples of these special characters right now
        // data = data.replace("","S").replaceAll("\\x27(\\W|\\s)", "&apos;$1");
        bill.setTitle(data.replace("\r\n", " ").trim());
    }

    public void applySameAs(String data, Bill bill, Date date) throws ParseError {
        // Like BillData lines, apply them in order because of DELETE
        for(String line : data.split("\r\n")) {
            if (line.trim().equals("No same as") || line.trim().startsWith("DELETE")) {
                bill.setSameAs("");

            } else {
                // Why do we do this, don't have an example of this issue..
                // line = line.replace("/", ",").replaceAll("[ \\-.;]", "");
                Matcher sameAs = sameAsPattern.matcher(line);
                if (sameAs.find()) {
                    bill.setSameAs(sameAs.group(2).replace("-","").replace(" ",""));
                } else {
                    logger.error("sameAsPattern not matched: "+data);
                }
            }
        }
    }
}
