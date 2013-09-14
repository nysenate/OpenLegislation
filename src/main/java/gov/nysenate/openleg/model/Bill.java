package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author GraylinKim
 */
public class Bill extends BaseObject implements Comparable<Bill>
{
    /**
     * The unique bill id.
     */
    protected String billId = "";

    /**
     * The bill title.
     */
    protected String title = "";

    /**
     * The section of the law this bill affects
     */
    protected String lawSection = "";

    /**
     * The "sameAs" bill in the other chamber
     */
    protected String sameAs = "";

    /**
     * A list of ids of versions of this legislation in previous sessions.
     */
    protected List<String> previousVersions = new ArrayList<String>();

    /**
     * The sponsor of this bill.
     */
    protected Person sponsor;

    /**
     * A list of coSponsors to be given preferential display treatment.
     */
    private List<Person> otherSponsors = new ArrayList<Person>();

    /**
     *
     */
    public List<String> amendments = new ArrayList<String>();

    /**
     * A list of cosponsors for the bill.
     */
    protected List<Person> coSponsors = new ArrayList<Person>();

    /**
     * A list of multi-sponsors for assembly legislation.
     */
    protected List<Person> multiSponsors = new ArrayList<Person>();

    /**
     * The summary of the bill
     */
    protected String summary = "";

    /**
     * The committee the bill is currently referred to, if any.
     */
    protected String currentCommittee = "";

    /**
     * A list of committees this bill has been referred to.
     */
    protected List<String> pastCommittees = new ArrayList<String>();

    /**
     * A list of actions that have been made on this bill.
     */
    protected List<Action> actions = new ArrayList<Action>();

    /**
     * The full text of the bill.
     */
    protected String fulltext = "";

    /**
     * The bill's sponsor memo.
     */
    protected String memo = "";

    /**
     * The law code modification summary.
     */
    protected String law = "";

    /**
     * The AN ACT TO... clause for the bill.
     */
    protected String actClause = "";

    /**
     * A list of votes that have been made on this bill.
     */
    protected List<Vote> votes = new ArrayList<Vote>();

    /**
     * A flag marking this bill as stricken (effectively withdrawn)
     */
    protected Boolean stricken = false;

    /**
     * A flag marking this bill as a uniBill with a companion bill in the other house.
     */
    private boolean uniBill = false;

    /**
     * JavaBean Constructor
     */
    public Bill()
    {
        super();
    }

    /**
     * Constructs a minimal Bill object.
     *
     * @param senateBillNo - The unique bill id
     * @param year - The session year this bill was introduced to
     */
    public Bill(String billId, int session)
    {
        this.setBillId(billId);
        this.setSession(session);
        this.setYear(session);
    }

    /**
     * @return - True if this bill is a resolution of some sort.
     */
    public boolean isResolution()
    {
        return billId.charAt(0)!='A' && billId.charAt(0)!='S';
    }

    /**
     * @return - The URL used to construct Disqus threads
     */
    public String getDisqusUrl()
    {
        if (this.getSession()==2009) {
            String disqusId = this.getBillId().split("-")[0];
            return "http://open.nysenate.gov/legislation/api/html/bill/" + disqusId;
        }
        else {
            String disqusId = this.getBillId();
            return "http://open.nysenate.gov/legislation/bill/" + disqusId;
        }
    }

    /**
     * @return - The bill's unique id.
     */
    public String getBillId()
    {
        return billId;
    }

    /**
     * @param billId - The new bill id.
     */
    public void setBillId(String billId)
    {
        this.billId = billId;
    }

    /**
     * The object type of the bill.
     */
    public String getOtype()
    {
        return "bill";
    }

    /**
     * @return - The unique object id.
     */
    public String getOid()
    {
        return this.getBillId();
    }

    /**
     * @return - The billId padded to 5 digits with zeros.
     */
    public String getPaddedBillId()
    {
        return this.getPaddedPrintNumber()+"-"+this.getSession();
    }

    /**
     * @return - The print number padded to 5 digits with zeros.
     */
    public String getPaddedPrintNumber()
    {
        Matcher billIdMatcher = billIdPattern.matcher(this.getBillId());
        if (billIdMatcher.find()) {
            return String.format("%s%05d%s", billIdMatcher.group(2), Integer.parseInt(billIdMatcher.group(3)), billIdMatcher.group(4));
        }
        else {
            // logger.warn("Invalid senateBillNo: "+this.senateBillNo);
            return "";
        }
    }

    /**
     * @return - Just the print number without the session year suffix.
     */
    public String getPrintNumber()
    {
        Matcher billIdMatcher = billIdPattern.matcher(this.getBillId());
        if (billIdMatcher.find()) {
            return billIdMatcher.group(1);
        }
        else {
            // logger.warn("Invalid senateBillNo: "+this.senateBillNo);
            return "";
        }
    }

    /**
     * @return - A list of billIds for the various other versions of this bill.
     */
    public List<String> getAmendments()
    {
        return this.amendments;
    }

    /**
     * @param amendments - The new list of billIds for the various other versions of this bill.
     */
    public void setAmendments(List<String> amendments)
    {
        this.amendments = amendments;
    }

    /**
     * @param amendments - A list of additional billIds for the various other versions of this bill.
     */
    public void addAmendments(List<String> amendments)
    {
        this.amendments.addAll(amendments);
    }

    /**
     * @param amendment - The new billId to add to our list of amendments.
     */
    public void addAmendment(String amendment)
    {
        this.amendments.add(amendment);
    }

    /**
     * @param amendment - The billId to remove from our list of amendments.
     */
    public void removeAmendment(String amendment)
    {
        this.amendments.remove(amendment);
    }

    /**
     * @return - The current bill title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title - The new bill title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return - The current law section.
     */
    public String getLawSection()
    {
        return lawSection;
    }

    /**
     * @param lawSection - The new law section
     */
    public void setLawSection(String lawSection)
    {
        this.lawSection = lawSection;
    }

    /**
     * @return - The same as bill ID.
     */
    public String getSameAs()
    {
        return sameAs;
    }

    /**
     * @param sameAs - The new same as bill ID.
     */
    public void setSameAs(String sameAs)
    {
        this.sameAs = sameAs;
    }

    /**
     * @return - The current list of bill ids of previous bill versions.
     */
    public List<String> getPreviousVersions()
    {
        return previousVersions;
    }

    /**
     * @param previousVersions - The new list of bill IDs of previous versions.
     */
    public void setPreviousVersions(List<String> previousVersions)
    {
        this.previousVersions = previousVersions;
    }

    /**
     * @param previousVersion - The new bill ID to add to the previous versions list.
     */
    public void addPreviousVersion(String previousVersion) {
        if(!previousVersions.contains(previousVersion)) {
            previousVersions.add(previousVersion);
        }
    }

    /**
     * @return - The current bill sponsor.
     */
    public Person getSponsor()
    {
        return sponsor;
    }

    /**
     * @param sponsor - The new bill sponsor.
     */
    public void setSponsor(Person sponsor)
    {
        this.sponsor = sponsor;
    }

    /**
     * @return - The current list of "other" sponsors.
     */
    public List<Person> getOtherSponsors()
    {
        return otherSponsors;
    }

    /**
     * @param otherSponsors - The new list of "other" sponsors.
     */
    public void setOtherSponsors(List<Person> otherSponsors)
    {
        this.otherSponsors = otherSponsors;
    }

    /**
     * @return - The current list of cosponsors.
     */
    public List<Person> getCoSponsors()
    {
        return coSponsors;
    }

    /**
     * @param coSponsors - The new list of cosponsors.
     */
    public void setCoSponsors(List<Person> coSponsors)
    {
        this.coSponsors = coSponsors;
    }

    /**
     * @return - The current list of multisponsors.
     */
    public List<Person> getMultiSponsors()
    {
        return multiSponsors;
    }

    /**
     * @param multiSponsors - The new list of multisponsors.
     */
    public void setMultiSponsors(List<Person> multiSponsors)
    {
        this.multiSponsors = multiSponsors;
    }

    /**
     * @return - The current bill summary.
     */
    public String getSummary()
    {
        return summary;
    }

    /**
     * @param summary - The new bill summary.
     */
    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    /**
     * @return - The current list of bill actions.
     */
    public List<Action> getActions()
    {
        return actions;
    }

    /**
     * @param actions - The new list of bill actions.
     */
    public void setActions(List<Action> actions)
    {
        this.actions = actions;
    }

    /**
     * @param action - The action to add to the list of bill actions.
     */
    public void addAction(Action action) {
        actions.add(action);
    }

    /**
     * @return - The current full text of the bill
     */
    public String getFulltext()
    {
        return fulltext;
    }

    /**
     * @param fulltext - The new full text of the bill.
     */
    public void setFulltext(String fulltext)
    {
        this.fulltext = fulltext;
    }

    /**
     * @return - The current sponsor memo for the bill.
     */
    public String getMemo()
    {
        return memo;
    }

    /**
     * @param memo - The new sponsor memo for the bill.
     */
    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    /**
     * @return - The current law modifications for the bill.
     */
    public String getLaw()
    {
        return law;
    }

    /**
     * @param law - The new law modifications for the bill.
     */
    public void setLaw(String law)
    {
        this.law = law;
    }

    /**
     * @return - The current AN ACT TO clause for the bill.
     */
    public String getActClause()
    {
        return actClause;
    }

    /**
     * @param actClause - The new AN ACT TO clause for the bill.
     */
    public void setActClause(String actClause)
    {
        this.actClause = actClause;
    }

    /**
     * @return - true if the bill has been stricken.
     */
    public Boolean isStricken()
    {
        return stricken;
    }

    /**
     * @param stricken - The new stricken state for the bill.
     */
    public void setStricken(Boolean stricken)
    {
        this.stricken = stricken;
    }

    /**
     * @return - true if the bill is a uniBill.
     */
    public boolean isUniBill()
    {
        return uniBill;
    }

    /**
     * @param uniBill - The new uniBill state for the bill.
     */
    public void setUniBill(boolean uniBill)
    {
        this.uniBill = uniBill;
    }

    /**
     * @return - The current committee for the bill.
     */
    public String getCurrentCommittee()
    {
        return currentCommittee;
    }

    /**
     * @param currentCommittee - The new current committee for the bill.
     */
    public void setCurrentCommittee(String currentCommittee)
    {
        this.currentCommittee = currentCommittee;
    }

    /**
     * @return - The current list of past committees.
     */
    public List<String> getPastCommittees()
    {
        return pastCommittees;
    }

    /**
     * @param pastCommittees - The new list of past committees.
     */
    public void setPastCommittees(List<String> pastCommittees)
    {
        this.pastCommittees = new ArrayList<String>();
        for(String pc:pastCommittees) {
            this.addPastCommittee(pc);
        }
    }

    /**
     * @param committee - The name of the new committee to add to past committees.
     */
    public void addPastCommittee(String committee)
    {
        if(!pastCommittees.contains(committee)) {
            pastCommittees.add(committee);
        }
    }

    /**
     * @return - The current list of votes on this bill.
     */
    public List<Vote> getVotes()
    {
        return votes;
    }

    /**
     * @param votes - The new list of votes on this bill.
     */
    public void setVotes(List<Vote> votes)
    {
        this.votes = votes;
    }

    /**
     * @param vote - The new vote to add to the list of votes on this bill. Replaces exiting vote data if a match is found.
     */
    public void updateVote(Vote vote) {
        votes.remove(vote);
        votes.add(vote);
    }

    /**
     * @param vote - The vote to remove from the list of votes on this bill.
     */
    public void removeVote(Vote vote) {
        votes.remove(vote);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Bill) {
            Bill other = (Bill)obj;
            return this.getBillId().equals(other.getBillId());
        }
        else {
            return false;
        }
    }


    public static Pattern printNumberPattern = Pattern.compile("([ASLREJKBC])([0-9]{1,5})([A-Z]?)");
    public static Pattern billIdPattern = Pattern.compile("("+printNumberPattern.pattern()+")-([0-9]{4})");


    @Override
    public int compareTo(Bill bill)
    {
        return this.getBillId().compareTo(bill.getBillId());
    }

    /**
     * if term looks like a bill number attempts to format to
     * <billNo>-<sessionYear> format
     * @param term the term being searched
     * @return formatted bill number or original term
     */
    public static String formatBillNo(String term)
    {
        Pattern p = Pattern.compile("^((?i)[sajr]\\W?0*\\d+[a-zA-Z]?)(?:\\-)?(\\d{4})?$");
        Matcher m = p.matcher(term);

        if(m.find()) {
            String bill = m.group(1).replaceAll("[^a-zA-Z\\d]","")
                    .replaceAll("^((?i)[sajr])0*", "$1");

            int year = (m.group(2) == null
                    ? SessionYear.getSessionYear()
                            : SessionYear.getSessionYear(new Integer(m.group(2))));

            return TextFormatter.append(bill, "-", year);
        }
        return term;
    }
}
