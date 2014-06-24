package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.entity.Person;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Bill class serves as a container for all the entities that can be classified under a print number
 * and session year. It contains a collection of amendments (including the base amendment) as well as
 * shared information such as the sponsor or actions.
 */
public class Bill extends BaseObject implements Comparable<Bill>
{
    public static Pattern printNumberPattern = Pattern.compile("([ASLREJKBC])([0-9]{1,5})([A-Z]?)");

    /** The default amendment version letter. */
    public static final String BASE_VERSION = "";

    /** The print number of the bill, e.g S1234 */
    protected String printNo = "";

    /** The bill title. */
    protected String title = "";

    /** The section of the law the bill affects. */
    protected String lawSection = "";

    /** The law code of the bill. */
    protected String law = "";

    /** The summary of the bill. */
    protected String summary = "";

    /** Map of amendment version -> Amendment. */
    protected Map<String, BillAmendment> amendmentMap = new TreeMap<>();

    /** Indicates the amendment version that is active for this bill. */
    protected String activeVersion = BASE_VERSION;

    /** A list of ids of versions of this legislation in previous sessions. */
    protected List<String> previousVersions = new ArrayList<>();

    /** The sponsor of this bill. */
    protected Person sponsor;

    /** A list of coSponsors to be given preferential display treatment. */
    private List<Person> otherSponsors = new ArrayList<>();

    /** A list of committees this bill has been referred to. */
    protected List<String> pastCommittees = new ArrayList<>();

    /** A list of actions that have been made on this bill. */
    protected List<BillAction> actions = new ArrayList<>();

    /** --- Constructors --- */

    public Bill() {
        super();
    }

    public Bill(String printNo, int sessionYear) {
        this.printNo = printNo;
        this.session = sessionYear;
        this.year = sessionYear;
    }

    /** --- Functional Getters/Setter --- */

    /**
     * Indicates if this bill is currently set to the base version.
     * @param version The bill version
     * @return Returns true if the version will be represented as a base bill
     */
    public static boolean isBaseVersion(String version) {
        return version == null || version.equals(BASE_VERSION);
    }

    /**
     * Creates a unique id for the bill by using printNo and session year, e.g S1234-2013
     * @return - The bill's unique id.
     */
    public String getBillId() {
        return this.printNo + "-" + this.session;
    }

    /**
     * Indicate if this bill is a resolution by checking that it's not a senate/assembly bill.
     * @return - True if this bill is a resolution of some sort.
     */
    public boolean isResolution() {
        return this.printNo.charAt(0) != 'A' && this.printNo.charAt(0) != 'S';
    }

    /**
     * Creates a unique id for the bill with padding to resemble LBDC's representation.
     * @return - The billId padded to 5 digits with zeros.
     */
    public String getPaddedBillId() {
        return this.getPaddedPrintNumber() + "-" + this.getSession();
    }

    /**
     * Returns the print number padded with 5 zeros, e.g S01234. This is how LBDC represents
     * print numbers in their SOBI files.
     * @return - The print number padded to 5 digits with zeros.
     */
    public String getPaddedPrintNumber() {
        Matcher billIdMatcher = printNumberPattern.matcher(this.getPrintNo());
        if (billIdMatcher.find()) {
            return String.format("%s%05d%s", billIdMatcher.group(1), Integer.parseInt(billIdMatcher.group(2)),
                                             billIdMatcher.group(3));
        }
        return "";
    }

    /**
     * Retrieves an amendment stored in this bill using the version as the key.
     * @param version - The amendment version of the bill (e.g "A", "B", etc)
     * @return BillAmendment if found, null otherwise
     */
    public BillAmendment getAmendment(String version) {
        return this.amendmentMap.get(version.toUpperCase());
    }

    /**
     * Retrieves a list of all amendments stored in this bill.
     * @return - List<BillAmendment>
     */
    public List<BillAmendment> getAmendmentList() {
        return new ArrayList<>(this.amendmentMap.values());
    }

    /**
     * Associate an amendment with this bill.
     * @param billAmendment - Amendment to add to this bill.
     */
    public void addAmendment(BillAmendment billAmendment) {
        this.amendmentMap.put(billAmendment.getVersion().toUpperCase(), billAmendment);
    }

    /**
     * Associate a list of amendments with this bill.
     * @param billAmendments - List<Amendment> - Amendments to add to this bill
     */
    public void addAmendments(List<BillAmendment> billAmendments) {
        for (BillAmendment billAmendment : billAmendments) {
            this.addAmendment(billAmendment);
        }
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
     * Convenience method to retrieve the currently active Amendment object.
     * @return BillAmendment
     */
    public BillAmendment getActiveAmendment() {
        return this.getAmendment(this.getActiveVersion());
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
     * Add an action to the list of actions.
     * @param action BillAction
     */
    public void addAction(BillAction action) {
        actions.add(action);
    }

    /**
     * Adds a committee to the list of past committees.
     * @param committee
     */
    public void addPastCommittee(String committee) {
        if(!pastCommittees.contains(committee)) {
            pastCommittees.add(committee);
        }
    }

    /** --- Delegates --- */

    public String getFulltext() {
        return this.getActiveAmendment().getFulltext();
    }

    /** --- Basic Getters/Setters --- */

    public String getPrintNo() {
        return printNo;
    }

    public void setPrintNo(String printNo) {
        this.printNo = printNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLawSection() {
        return lawSection;
    }

    public void setLawSection(String lawSection) {
        this.lawSection = lawSection;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(String activeVersion) {
        this.activeVersion = activeVersion;
    }

    public Map<String, BillAmendment> getAmendmentMap() {
        return amendmentMap;
    }

    public List<String> getPreviousVersions() {
        return previousVersions;
    }

    public void setPreviousVersions(List<String> previousVersions) {
        this.previousVersions = previousVersions;
    }

    public List<BillAction> getActions() {
        return actions;
    }

    public void setActions(List<BillAction> actions) {
        this.actions = actions;
    }

    public Person getSponsor() {
        return sponsor;
    }

    public void setSponsor(Person sponsor) {
        this.sponsor = sponsor;
    }

    public List<String> getPastCommittees() {
        return pastCommittees;
    }

    public void setPastCommittees(List<String> pastCommittees) {
        this.pastCommittees = new ArrayList<>(pastCommittees);
    }

    public List<Person> getOtherSponsors() {
        return otherSponsors;
    }

    public void setOtherSponsors(List<Person> otherSponsors) {
        this.otherSponsors = otherSponsors;
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
    public void setPublishDate(Date publishDate) {
        throw new RuntimeException("Cannot set publish on the bill container.");
    }

    @Override
    @JsonIgnore
    public Date getModifiedDate() {
        for (BillAmendment amendment : amendmentMap.values()) {
            if (amendment.getModifiedDate().after(this.modifiedDate)) {
                return amendment.getModifiedDate();
            }
        }
        return this.modifiedDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Bill) {
            Bill other = (Bill)obj;
            return this.getBillId().equals(other.getBillId());
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Bill bill) {
        return this.getBillId().compareTo(bill.getBillId());
    }

    /**
     * if term looks like a bill number attempts to format to
     * <billNo>-<sessionYear> format
     * @param term the term being searched
     * @return formatted bill number or original term
     */
    public static String formatBillNo(String term) {
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