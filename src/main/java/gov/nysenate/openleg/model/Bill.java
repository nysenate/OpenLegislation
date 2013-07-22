package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.openleg.xstream.BillListConverter;
import gov.nysenate.openleg.xstream.XStreamCollectionAlias;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("bill")
public class Bill extends BaseObject implements Comparable<Bill>
{
    protected boolean active = true;

    protected int year;

    @XStreamAlias("senateId")
    protected String billId = "";

    protected String title = "";

    protected String lawSection = "";

    protected String sameAs = "";

    @XStreamConverter(BillListConverter.class)
    protected List<String> previousVersions = new ArrayList<String>();

    protected Person sponsor;

    private List<Person> otherSponsors = new ArrayList<Person>();

    public boolean frozen = false;

    public List<String> amendments = new ArrayList<String>();

    @XStreamAlias("cosponsors")
    protected List<Person> coSponsors = new ArrayList<Person>();

    @XStreamAlias("multisponsors")
    protected List<Person> multiSponsors = new ArrayList<Person>();

    protected String summary = "";

    @XStreamAlias("committee")
    protected String currentCommittee = "";

    protected List<String> pastCommittees = new ArrayList<String>();

    @XStreamConverter(BillListConverter.class)
    protected List<Action> actions = new ArrayList<Action>();

    @XStreamAlias("text")
    protected String fulltext = "";

    protected String memo = "";

    protected String law = "";

    protected String actClause = "";

    @XStreamCollectionAlias(node="votes",value="vote")
    protected List<Vote> votes = new ArrayList<Vote>();

    protected Boolean stricken = false;

    private boolean uniBill = false;

    public Bill()
    {
        super();
    }

    public Bill(String senateBillNo, int year) {
        this.billId = senateBillNo;
        this.year = year;
    }

    @JsonIgnore
    public boolean isResolution() {
        return billId.charAt(0)!='A' && billId.charAt(0)!='S';
    }

    @JsonIgnore
    public String getDisqusUrl() {
        if (this.getYear()==2009) {
            String disqusId = this.getBillId().split("-")[0];
            return "http://open.nysenate.gov/legislation/api/html/bill/" + disqusId;
        }
        else {
            String disqusId = this.getBillId();
            return "http://open.nysenate.gov/legislation/bill/" + disqusId;
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }


    public String getBillId()
    {
        return billId;
    }

    public void setBillId(String billId)
    {
        this.billId = billId;
    }

    @JsonIgnore
    public String getOid()
    {
        return this.getBillId();
    }

    @JsonIgnore
    public String getPaddedBillId()
    {
        return this.getPaddedPrintNumber()+"-"+this.getYear();
    }

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

    public List<String> getAmendments()
    {
        return this.amendments;
    }

    public void setAmendments(List<String> amendments)
    {
        this.amendments = amendments;
    }

    public void addAmendments(List<String> amendments)
    {
        this.amendments.addAll(amendments);
    }

    public void addAmendment(String amendment)
    {
        this.amendments.add(amendment);
    }

    public void removeAmendment(String amendment)
    {
        this.amendments.remove(amendment);
    }


    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }


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
        return sameAs;
    }

    public void setSameAs(String sameAs)
    {
        this.sameAs = sameAs;
    }


    public List<String> getPreviousVersions()
    {
        return previousVersions;
    }

    public void setPreviousVersions(List<String> previousVersions)
    {
        this.previousVersions = previousVersions;
    }

    public void addPreviousVersion(String previousVersion) {
        if(!previousVersions.contains(previousVersion)) {
            previousVersions.add(previousVersion);
        }
    }


    public Person getSponsor()
    {
        return sponsor;
    }

    public void setSponsor(Person sponsor)
    {
        this.sponsor = sponsor;
    }

    public List<Person> getOtherSponsors()
    {
        return otherSponsors;
    }

    public void setOtherSponsors(List<Person> otherSponsors)
    {
        this.otherSponsors = otherSponsors;
    }

    public List<Person> getCoSponsors()
    {
        return coSponsors;
    }

    public void setCoSponsors(List<Person> coSponsors)
    {
        this.coSponsors = coSponsors;
    }


    public List<Person> getMultiSponsors()
    {
        return multiSponsors;
    }

    public void setMultiSponsors(List<Person> multiSponsors)
    {
        this.multiSponsors = multiSponsors;
    }


    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }


    public List<Action> getActions()
    {
        return actions;
    }

    public void setActions(List<Action> actions)
    {
        this.actions = actions;
    }

    public void addAction(Action be) {
        actions.add(be);
    }


    public String getFulltext()
    {
        return fulltext;
    }

    public void setFulltext(String fulltext)
    {
        this.fulltext = fulltext;
    }


    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }


    public String getLaw()
    {
        return law;
    }

    public void setLaw(String law)
    {
        this.law = law;
    }


    public String getActClause()
    {
        return actClause;
    }

    public void setActClause(String actClause)
    {
        this.actClause = actClause;
    }


    public Boolean isStricken()
    {
        return stricken;
    }

    public void setStricken(Boolean stricken)
    {
        this.stricken = stricken;
    }

    public boolean isUniBill()
    {
        return uniBill;
    }

    public void setUniBill(boolean uniBill)
    {
        this.uniBill = uniBill;
    }

    public String getCurrentCommittee()
    {
        return currentCommittee;
    }

    public void setCurrentCommittee(String currentCommittee)
    {
        this.currentCommittee = currentCommittee;
    }


    public List<String> getPastCommittees()
    {
        return pastCommittees;
    }

    public void setPastCommittees(List<String> pastCommittees)
    {
        this.pastCommittees = new ArrayList<String>();
        for(String pc:pastCommittees) {
            this.addPastCommittee(pc);
        }
    }

    public void addPastCommittee(String committee)
    {
        if(!pastCommittees.contains(committee)) {
            pastCommittees.add(committee);
        }
    }


    public List<Vote> getVotes()
    {
        return votes;
    }

    public void setVotes(List<Vote> votes)
    {
        this.votes = votes;
    }

    public void addVote(Vote vote) {
        votes.remove(vote);
        votes.add(vote);
    }

    public void removeVote(Vote vote) {
        votes.remove(vote);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Bill) {
            return this.getBillId().equals(((Bill)obj).getBillId());
        }
        else {
            return false;
        }
    }

    public static Pattern printNumberPattern = Pattern.compile("([ASLREJKBC])([0-9]{1,5})([A-Z]?)");
    public static Pattern billIdPattern = Pattern.compile("("+printNumberPattern.pattern()+")-([0-9]{4})");

    @JsonIgnore
    public String getKey()
    {
        return Bill.getKey(this.getBillId());
    }

    @JsonIgnore
    public static String getKey(String billNo)
    {
        Matcher keyMatcher = billIdPattern.matcher(billNo);
        if (keyMatcher.find()) {
            return keyMatcher.group(5)+"/bill/"+keyMatcher.group(0);
        }
        else {
            System.out.println("COULD NOT PARSE senateBillNo: "+billNo);
            return null;
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










