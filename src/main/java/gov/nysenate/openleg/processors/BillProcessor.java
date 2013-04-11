package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class BillProcessor extends AbstractBillProcessor
{
    private final Logger logger = Logger.getLogger(BillProcessor.class);

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


    /**
     * Applies the data to the bill summary. Strips out all whitespace formatting.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applySummary(String data, Bill bill, Date date) throws ParseError
    {
        // The DELETE code for the summary goes through the law block (B)
        // Combine the lines with a space and handle special character issues..
        // I don't have any examples of these special characters right now, here is some legacy code:
        //      data = data.replace("","S").replaceAll("\\x27(\\W|\\s)", "&apos;$1");
        bill.setSummary(data.replace("\n", " ").trim());
    }

    /**
     * Applies the data to the bill title. Strips out all whitespace formatting.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyTitle(String data, Bill bill, Date date) throws ParseError
    {
        // No DELETE code for titles, they just get replaced
        // Combine the lines with a space and handle special character issues..
        // I don't have any examples of these special characters right now, here is some legacy code:
        //      data = data.replace("","S").replaceAll("\\x27(\\W|\\s)", "&apos;$1");
        bill.setTitle(data.replace("\n", " ").trim());
    }

    /**
     * Applies data to the bill Same-as.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applySameAs(String data, Bill bill, Date date) throws ParseError
    {
        // Guarenteed to be only a single line block.
        if (data.trim().equals("No same as") || data.trim().equals("DELETE")) {
            bill.setSameAs("");
        }
        else {
            // Why do we do this, don't have an example of this issue, here is some legacy code:
            //     line = line.replace("/", ",").replaceAll("[ \\-.;]", "");
            Matcher sameAsMatcher = sameAsPattern.matcher(data);
            if (sameAsMatcher.find()) {
                bill.setSameAs(sameAsMatcher.group(2).replace("-","").replace(" ",""));
            } else {
                logger.error("sameAsPattern not matched: "+data);
            }
        }
    }

    /**
     * Applies data to bill sponsor. Fully replaces existing sponsor information.
     *
     * A delete in these field removes all sponsor information.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applySponsor(String data, Bill bill, Date date) throws ParseError
    {
        // Apply the lines in order given as each represents its own "block"
        for(String line : data.split("\n")) {
            if (line.trim().equals("DELETE")) {
                bill.setSponsor(null);
                bill.setCoSponsors(new ArrayList<Person>());
                bill.setMultiSponsors(new ArrayList<Person>());

            } else {
                bill.setSponsor(new Person(line.trim()));
            }
        }
    }

    /**
     * Applies data to bill co-sponsors. Fully replaces existing information.
     *
     * Delete code is sent through the sponsor block.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyCosponsors(String data, Bill bill, Date date) throws ParseError
    {
        ArrayList<Person> coSponsors = new ArrayList<Person>();
        if (!data.trim().isEmpty()) {
            for(String coSponsor : data.replace("\n", " ").split(",")) {
                coSponsors.add(new Person(coSponsor.trim()));
            }
        }
        bill.setCoSponsors(coSponsors);
    }

    /**
     * Applies data to bill multi-sponsors. Fully replaces existing information.
     *
     * Delete code is sent through the sponsor block.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyMultisponsors(String data, Bill bill, Date date) throws ParseError
    {
        ArrayList<Person> multiSponsors = new ArrayList<Person>();
        if (!data.trim().isEmpty()) {
            for(String multiSponsor : data.replace("\n", " ").split(",")) {
                multiSponsors.add(new Person(multiSponsor.trim()));
            }
        }
        bill.setMultiSponsors(multiSponsors);
    }


    /**
     * Applies data to bill law. Fully replaces existing information.
     *
     * DELETE code here also deletes the bill summary.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyLaw(String data, Bill bill, Date date) throws ParseError
    {
        // This is theoretically not safe because a law line *could* start with DELETE but
        // in practice this doesn't ever happen.
        // We can't do an exact match because B can be multiline
        if (data.trim().startsWith("DELETE")) {
            bill.setLaw("");
            bill.setSummary("");

        } else {
            // We'll definitely need to clean this data up more than a little bit, these encoding issues are terrible!
            // data = data.replaceAll("\\xBD", ""); // I dont' think we still need this
            bill.setLaw(data.replace("\n", " ").replace("õ", "S").replace("ô","P").replace("ï¿½","S").replace((char)65533+"", "S").trim());
        }
    }

    /**
     * Applies data to law section. Fully replaces existing data.
     *
     * Delete code is sent through the law block.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyLawSection(String data, Bill bill, Date date) throws ParseError
    {
        bill.setLawSection(data.trim());
    }

    /**
     * Applies data to the ACT TO clause. Fully replaces existing data.
     *
     * DELETE code removes existing ACT TO clause.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyActClause(String data, Bill bill, Date date) throws ParseError
    {
        if (data.trim().equals("DELETE")) {
            bill.setActClause("");
        } else {
            bill.setActClause(data.replace("\n", " ").trim());
        }
    }

    /**
     * Applies data to bill Program. Fully replaces existing information
     *
     *      029 Governor Program
     *
     * Currently implemented.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyProgramInfo(String data, Bill bill, Date date) throws ParseError
    {
        // This information currently isn't used for anything
        //if (!data.equals(""))
        //    throw new ParseError("Program info not implemented", data);
    }


    /**
     * Apply information from the Bill Info block. Fully replaces existing information.
     *
     * Currently only uses sponsor and previous version (which has known issues)
     *
     * A DELETE code sent with this block removes the whole bill.
     *
     * @param data
     * @param bill
     * @param date
     * @throws ParseError
     */
    public void applyBillInfo(String data, Bill bill, Date date) throws ParseError
    {
        // The DELETE code here applies to the whole bill and is handled in ``process``
        // Guarenteed to be only a 1 line block

        // The null bytes screw with the regex, replace them.
        Matcher billData = billDataPattern.matcher(data);
        if (billData.find()) {
            //TODO: Find a possible use for this information
            String sponsor = billData.group(1).trim();
            //String reprint = billData.group(2);
            //String blurb = billData.group(3);
            String oldbill = billData.group(4).trim().replaceAll("[0-9`-]$", "");
            //String lbdnum = billData.group(5);

            if (!sponsor.isEmpty() && bill.getSponsor() == null) {
                bill.setSponsor(new Person(sponsor));
            }
            bill.addPreviousVersion(oldbill);
        } else {
            throw new ParseError("billDataPattern not matched by "+data);
        }
    }

    public void applyVoteMemo(String data, Bill bill, Date date) throws ParseError
    {
        // TODO: Parse out sequence number once LBDC includes it, #6531
        // Example of a double vote entry: SOBI.D110119.T140802.TXT:390
        Vote vote = null;
        for(String line : data.split("\n")) {
            Matcher voteHeader = voteHeaderPattern.matcher(line);
            if (voteHeader.find()) {
                //Start over if we hit a header, sometimes we get back to back entries
                try {
                    vote = new Vote(bill, voteDateFormat.parse(voteHeader.group(2)), Vote.VOTE_TYPE_FLOOR, "1");
                }
                catch (ParseException e) {
                    throw new ParseError("voteDateFormat not matched: "+line);
                }
            }
            else if (vote!=null){
                //Otherwise, build the existing vote
                Matcher voteLine = votePattern.matcher(line);
                while(voteLine.find()) {
                    String type = voteLine.group(1).trim();
                    Person voter = new Person(voteLine.group(2).trim());

                    if (type.equals("Aye")) {
                        vote.addAye(voter);
                    }
                    else if (type.equals("Nay")) {
                        vote.addNay(voter);
                    }
                    else if (type.equals("Abs")) {
                        vote.addAbsent(voter);
                    }
                    else if (type.equals("Abd")) {
                        vote.addAbstain(voter);
                    }
                    else if (type.equals("Exc")) {
                        vote.addExcused(voter);
                    }
                    else {
                        throw new ParseError("Unknown vote type found: "+line);
                    }
                }

            } else {
                throw new ParseError("Hit vote data without a header: "+data);
            }
        }

        //Misnomer, will actually update the vote if it already exists
        bill.addVote(vote);
    }


    public void applyText(String data, Bill bill, Date date) throws ParseError
    {
        // BillText, ResolutionText, and MemoText can be handled the same way
        // Deleted with a *DELETE* line.
        String type = "";
        StringBuffer text = null;

        for (String line : data.split("\n")) {
            Matcher header = textPattern.matcher(line);
            if (line.startsWith("00000") && header.find()) {
                //TODO: If house == C then bills can be used for SAME AS verification
                // e.g. 2013S02278 T00000.SO DOC C 2279/2392
                // and  2013S02277 5Same as Uni. A 2394
                //String house = header.group(1);
                //String bills = header.group(2).trim();
                //String year = header.group(5);
                String action = header.group(3).trim();
                type = header.group(4).trim();


                if (!type.equals("BTXT") && !type.equals("RESO TEXT") && !type.equals("MTXT"))
                    throw new ParseError("Unknown text type found: "+type);

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
                        throw new ParseError("Text END Found before a body: "+line);
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
                    throw new ParseError("Unknown text action found: "+action);
                }
            } else if (text != null) {
                // Remove the leading numbers
                text.append(line.substring(5)+"\n");

            } else {
                throw new ParseError("Text Body found before header: "+line);
            }
        }

        if (text != null) {
            // This is a known issue that was resolved on 03/23/2011
            if (new GregorianCalendar(2011, 3, 23).after(date)) {
                throw new ParseError("Finished text data without a footer");


            } else {
                // Commit what we have and move on
                if (type.equals("BTXT") || type.equals("RESO TEXT")) {
                    bill.setFulltext(text.toString());
                } else if (type.equals("MTXT")) {
                    bill.setMemo(text.toString());
                }
            }
        }
    }


    public void applyBillEvent(String data, Bill bill, Date date) throws ParseError
    {
        // currently we don't want to keep track of assembly committees
        Boolean trackCommittees = !bill.getSenateBillNo().startsWith("A");

        ArrayList<Action> events = new ArrayList<Action>();
        String sameAs = "";
        Boolean stricken = false;
        String currentCommittee = "";
        List<String> pastCommittees = new ArrayList<String>();

        for (String line : data.split("\n")) {
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
                    throw new ParseError("eventDateFormat parse failure: "+billEvent.group(1));
                }
            } else {
                throw new ParseError("billEventPattern not matched: "+line);
            }
        }

        bill.setActions(events);
        bill.setSameAs(sameAs);
        bill.setCurrentCommittee(currentCommittee);
        bill.setPastCommittees(pastCommittees);
        bill.setStricken(stricken);
    }
}