package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Bill extends BaseObject implements Comparable<Bill>
{
    public static Pattern printNumberPattern = Pattern.compile("([ASLREJKBC])([0-9]{1,5})([A-Z]?)");
    public static Pattern billIdPattern = Pattern.compile("("+printNumberPattern.pattern()+")-([0-9]{4})");

    /** The default amendment version letter. */
    public static final String BASE_VERSION = "";

    /** Error message for when an invalid bill version is supplied as an argument */
    private static final String INVALID_AMENDMENT_VERSION = "Specified amendment version does not exist: ";

    /** The unique bill id. */
    protected String billId = "";

    /** The bill title. */
    protected String title = "";

    /** The section of the law the bill affects. */
    protected String lawSection = "";

    /** The summary of the amendment. */
    protected String summary = "";

    /** Map of amendment version -> Amendment. The default amendment should
     *  not be included in this mapping. */
    protected LinkedHashMap<String, BillAmendment> amendmentMap = new LinkedHashMap<>();

    /** Indicates the amendment version that is active for this bill. */
    protected String activeVersion = Bill.BASE_VERSION;

    /** Retains a history of versions that were activated */
    protected LinkedList<String> activeHistory = new LinkedList<>();

    /** A list of ids of versions of this legislation in previous sessions. */
    protected List<String> previousVersions = new ArrayList<String>();

    /** The sponsor of this bill. */
    protected Person sponsor;

    /** A list of coSponsors to be given preferential display treatment. */
    private List<Person> otherSponsors = new ArrayList<Person>();

    /** A list of committees this bill has been referred to. */
    protected List<String> pastCommittees = new ArrayList<String>();

    /** A list of actions that have been made on this bill. */
    protected List<BillAction> actions = new ArrayList<BillAction>();

    public Bill() {
        super();
    }

    /**
     * Constructs a minimal Bill object.
     *
     * @param billId - The unique bill id
     * @param session - The session year this bill was introduced to
     */
    public Bill(String billId, int session)
    {
        this();
        this.setBillId(billId);
        this.setSession(session);
        this.setYear(session);
    }

    /**
     * @param version The bill version
     * @return Returns true if the version will be represented as a base bill
     */
    @JsonIgnore
    public static boolean isBaseVersion(String version)
    {
        return version == null || version.equals(BASE_VERSION);
    }

    /**
     * @return - True if this bill is a resolution of some sort.
     */
    @JsonIgnore
    public boolean isResolution()
    {
        return billId.charAt(0)!='A' && billId.charAt(0)!='S';
    }

    /**
     * @return - The URL used to construct Disqus threads
     */
    @JsonIgnore
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
    @JsonIgnore
    public String getOtype()
    {
        return "bill";
    }

    /**
     * @return - The unique object id.
     */
    @JsonIgnore
    public String getOid()
    {
        return this.getBillId();
    }

    /**
     * @return - The billId padded to 5 digits with zeros.
     */
    @JsonIgnore
    public String getPaddedBillId()
    {
        return this.getPaddedPrintNumber()+"-"+this.getSession();
    }

    /**
     * @return - The print number padded to 5 digits with zeros.
     */
    @JsonIgnore
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
    @JsonIgnore
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
     * @param version - The amendment version of the bill (e.g "A", "B", etc)
     * @return Amendment if found, null otherwise
     */
    @JsonIgnore
    public BillAmendment getAmendment(String version)
    {
        return this.amendmentMap.get(version.toUpperCase());
    }

    /**
     * @return - A list of all Amendments associated with this Bill.
     */
    @JsonIgnore
    public List<BillAmendment> getAmendmentList()
    {
        return new ArrayList<>(this.amendmentMap.values());
    }

    /**
     * Associate an amendment with this bill.
     * @param billAmendment - Amendment to associate.
     */
    public void addAmendment(BillAmendment billAmendment)
    {
        this.amendmentMap.put(billAmendment.getVersion().toUpperCase(), billAmendment);
    }

    /**
     * Indicate whether the bill has a reference to a given amendment version.
     * @param version String - Amendment version
     * @return boolean
     */
    public boolean hasAmendment(String version)
    {
        return this.amendmentMap.containsKey(version.toUpperCase()) &&
               this.amendmentMap.get(version.toUpperCase()) != null;
    }

    /**
     * @return Mapping of Amendment versions to Amendment objects for keyed retrieval.
     */
    public Map<String, BillAmendment> getAmendmentMap()
    {
        return amendmentMap;
    }

    public BillAmendment getActiveAmendment()
    {
        return this.getAmendment(this.getActiveVersion());
    }

    /**
     * @return The active amendment version.
     */
    public String getActiveVersion()
    {
        return activeVersion;
    }

    /**
     * @param activeVersion - The new active amendment version.
     */
    public void setActiveVersion(String activeVersion)
    {
        this.activeVersion = activeVersion;
    }

    public LinkedList<String> getActiveHistory() {
        return activeHistory;
    }

    public void setActiveHistory(LinkedList<String> activeHistory) {
        this.activeHistory = activeHistory;
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

    public void setLawSection(String lawSection)
    {
        this.lawSection = lawSection;
    }

    public String getSameAs()
    {
        return this.getActiveAmendment().getSameAs();
    }

    public String getSameAs(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getSameAs();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    /**
     * @param sameAs - The new same as bill ID.
     */
    public void setSameAs(String sameAs, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setSameAs(sameAs);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
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

    public List<Person> getCoSponsors()
    {
        return this.getActiveAmendment().getCoSponsors();
    }

    public List<Person> getCoSponsors(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getCoSponsors();
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    /**
     * @param coSponsors - The new list of cosponsors.
     */
    public void setCoSponsors(List<Person> coSponsors, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setCoSponsors(coSponsors);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public List<Person> getMultiSponsors()
    {
        return this.getActiveAmendment().getMultiSponsors();
    }

    public List<Person> getMultiSponsorsList(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getMultiSponsors();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setMultiSponsors(List<Person> multiSponsors, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setMultiSponsors(multiSponsors);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public List<BillAction> getActions()
    {
        return actions;
    }

    public void setActions(List<BillAction> actions)
    {
        this.actions = actions;
    }

    public void addAction(BillAction action) {
        actions.add(action);
    }

    public String getFulltext()
    {
        return this.getActiveAmendment().getFulltext();
    }

    public String getFulltext(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getFulltext();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setFulltext(String fulltext, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setFulltext(fulltext);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public String getMemo()
    {
        return this.getActiveAmendment().getMemo();
    }

    public String getMemo(String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).getMemo();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setMemo(String memo, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setMemo(memo);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public String getLaw()
    {
        return this.getActiveAmendment().getLaw();
    }

    public String getLaw(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getLaw();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setLaw(String law, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setLaw(law);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public String getActClause(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getActClause();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setActClause(String actClause, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setActClause(actClause);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public Boolean isStricken(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).isStricken();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setStricken(Boolean stricken, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setStricken(stricken);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public boolean isUniBill(String version)
    {
        if (hasAmendment(version)) {
            return this.getAmendment(version).isUniBill();
        }
        else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public void setUniBill(boolean uniBill, String version)
    {
        if (hasAmendment(version)) {
            this.getAmendment(version).setUniBill(uniBill);
        }
        else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }

    }

    public String getCurrentCommittee()
    {
        return this.getActiveAmendment().getCurrentCommittee();
    }

    public String getCurrentCommittee(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getCurrentCommittee();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setCurrentCommittee(String currentCommittee, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setCurrentCommittee(currentCommittee);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    public List<String> getPastCommittees()
    {
        return pastCommittees;
    }

    public void setPastCommittees(List<String> pastCommittees)
    {
        this.pastCommittees = new ArrayList<String>(pastCommittees);
    }

    public void addPastCommittee(String committee)
    {
        if(!pastCommittees.contains(committee)) {
            pastCommittees.add(committee);
        }
    }

    public List<Vote> getVotes()
    {
        ArrayList<Vote> votes = new ArrayList<Vote>();
        for(BillAmendment amendment : this.getAmendmentList()) {
            votes.addAll(amendment.getVotes());
        }
        return votes;
    }

    public List<Vote> getVotes(String version)
    {
        if (hasAmendment(version)) {
            return getAmendment(version).getVotes();
        }
        throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
    }

    public void setVotes(List<Vote> votes, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).setVotes(votes);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    /**
     * @param vote - The new vote to add to the list of votes on this bill. Replaces exiting vote data if a match is found.
     */
    public void updateVote(Vote vote, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).getVotes().remove(vote);
            getAmendment(version).getVotes().add(vote);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    @Override
    @JsonIgnore
    public Date getPublishDate()
    {
        for (BillAmendment amendment : amendmentMap.values()) {
            if (amendment.getPublishDate() != null) {
                return amendment.getPublishDate();
            }
        }
        return null;
    }

    @Override
    public void setPublishDate(Date publishDate)
    {
        throw new RuntimeException("Cannot set publish on the bill container.");
    }

    @Override
    @JsonIgnore
    public Date getModifiedDate()
    {
        for (BillAmendment amendment : amendmentMap.values()) {
            if (amendment.getModifiedDate().after(this.modifiedDate)) {
                return amendment.getModifiedDate();
            }
        }
        return this.modifiedDate;
    }

    /**
     * @param vote - The vote to remove from the list of votes on this bill.
     */
    public void removeVote(Vote vote, String version)
    {
        if (hasAmendment(version)) {
            getAmendment(version).getVotes().remove(vote);
        } else {
            throw new IllegalArgumentException(INVALID_AMENDMENT_VERSION+version);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Bill) {
            Bill other = (Bill)obj;
            return this.getBillId().equals(other.getBillId());
        } else {
            return false;
        }
    }

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