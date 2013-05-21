package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.lucene.LuceneField;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.openleg.xstream.BillListConverter;
import gov.nysenate.openleg.xstream.XStreamCollectionAlias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("bill")
public class Bill extends SenateObject implements Comparable<Bill>
{

    protected boolean active = true;

    @LuceneField
    protected int year;

    @XStreamAlias("senateId")
    protected String senateBillNo = "";

    @LuceneField
    protected String title = "";

    @LuceneField
    protected String lawSection = "";

    @LuceneField
    protected String sameAs = "";

    @XStreamConverter(BillListConverter.class)
    protected List<String> previousVersions = new ArrayList<String>();

    @LuceneField
    protected Person sponsor;

    @LuceneField
    private List<Person> otherSponsors = new ArrayList<Person>();

    public boolean frozen = false;

    public List<String> amendments = new ArrayList<String>();

    @XStreamAlias("cosponsors")
    @LuceneField
    protected List<Person> coSponsors = new ArrayList<Person>();

    @XStreamAlias("multisponsors")
    @LuceneField
    protected List<Person> multiSponsors = new ArrayList<Person>();

    @LuceneField
    protected String summary = "";

    @XStreamAlias("committee")
    @LuceneField("committee")
    protected String currentCommittee = "";

    protected List<String> pastCommittees = new ArrayList<String>();

    @XStreamConverter(BillListConverter.class)
    @LuceneField()
    protected List<Action> actions = new ArrayList<Action>();

    @XStreamAlias("text")
    @LuceneField("full")
    protected String fulltext = "";

    @LuceneField
    protected String memo = "";

    @LuceneField
    protected String law = "";

    @LuceneField
    protected String actClause = "";

    @XStreamCollectionAlias(node="votes",value="vote")
    protected List<Vote> votes = new ArrayList<Vote>();

    @LuceneField
    protected Boolean stricken = false;

    @LuceneField
    private boolean uniBill = false;

    public Bill()
    {
        super();
    }

    public Bill(String senateBillNo, int year) {
        this.senateBillNo = senateBillNo;
        this.year = year;
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


    public String getSenateBillNo()
    {
        return senateBillNo;
    }

    public void setSenateBillNo(String senateBillNo)
    {
        this.senateBillNo = senateBillNo;
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
        if (!votes.contains(vote)) {
            votes.add(vote);
        }
    }

    public void removeVote(Vote vote) {
        votes.remove(vote);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Bill) {
            return this.getSenateBillNo().equals(((Bill)obj).getSenateBillNo());
        }
        else {
            return false;
        }
    }

    public static Pattern keyPattern = Pattern.compile("([ASLREJK][0-9]{1,5}[A-Z]?)-([0-9]{4})");

    @JsonIgnore
    public String getKey()
    {
        return Bill.getKey(this.getSenateBillNo());
    }

    @JsonIgnore
    public static String getKey(String billNo)
    {
        Matcher keyMatcher = keyPattern.matcher(billNo);
        if (keyMatcher.find()) {
            return keyMatcher.group(2)+"/bill/"+keyMatcher.group(0);
        }
        else {
            System.out.println("COULD NOT PARSE senateBillNo: "+billNo);
            return null;
        }
    }

    @JsonIgnore
    @Override
    public String luceneOtype()
    {
        return "bill";
    }

    @JsonIgnore
    @Override
    public String luceneOid()
    {
        if (senateBillNo.indexOf("-" + year)==-1) {
            return senateBillNo + "-" + year;
        }
        else {
            return senateBillNo;
        }
    }

    @JsonIgnore
    @Override
    public HashMap<String,Fieldable> luceneFields()
    {
        HashMap<String,Fieldable> map = new HashMap<String,Fieldable>();

        if(this.getPastCommittees() != null) {
            String pcoms = "";
            for(String committee:pastCommittees) {
                pcoms += committee + ", ";
            }
            pcoms.replaceFirst(", $", "");
            map.put("pastcommittees", new Field("pastcommittees",pcoms, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        String billStatus = "";
        if (!actions.isEmpty()) {
            billStatus = actions.get(actions.size()-1).getText();
        }
        map.put("status", new Field("status", billStatus, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        /*
         * the following creates a sortable index so we can sort
         * s1,s2,s3,s11 instead of s1,s11,s2,s3.  senate bills take
         * precedence, followed by assembly and finally anything else
         */
        String num = senateBillNo.split("-")[0];
        num = num.substring(1, (Character.isDigit(num.charAt(num.length()-1))) ? num.length() : num.length() - 1);
        while(num.length() < 6)
            num = "0" + num;

        if(senateBillNo.charAt(0) == 'S')
            num = "A" + num;
        else if(senateBillNo.charAt(0) == 'A')
            num = "B" + num;
        else
            num = "Z" + num;
        map.put("sortindex", new Field("sortindex",num, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        return map;
    }

    @JsonIgnore
    @Override
    public String luceneSummary()
    {
        return summary;
    }

    @JsonIgnore
    @Override
    public String luceneTitle()
    {
        return (title == null) ? summary : title;
    }

    @JsonIgnore
    @Override public String luceneOsearch()
    {
        return senateBillNo.split("-")[0] + " "
                + year + " "
                + senateBillNo + "-" + year
                + (sameAs != null ? " " + sameAs:"")
                + (sponsor != null ? " " + sponsor.getFullname():"")
                + (title != null ? " " + title:"")
                + (summary != null ? " " + summary:"");

    }

    @JsonIgnore
    public String getLuceneOtherSponsors()
    {
        return StringUtils.join(otherSponsors, ", ");
    }

    @JsonIgnore
    public String getLuceneCoSponsors()
    {
        if(this.getCoSponsors() == null)
            return "";

        StringBuilder response = new StringBuilder();
        for( Person sponsor : coSponsors) {
            response.append(sponsor.getFullname() + ", ");
        }
        return response.toString().replaceAll(", $", "");
    }

    @JsonIgnore
    public String getLuceneMultiSponsors()
    {
        if(this.getMultiSponsors() == null)
            return "";

        StringBuilder response = new StringBuilder();
        for( Person sponsor : multiSponsors) {
            response.append(sponsor.getFullname() + ", ");
        }
        return response.toString().replaceAll(", $", "");
    }

    @JsonIgnore
    public String getLuceneActions()
    {
        if(this.getActions() ==  null) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        for(Action be : actions) {
            response.append(be.getText() + ", ");
        }
        return response.toString().replaceAll(", $", "");
    }

    @JsonIgnore
    public String getLuceneSponsor()
    {
        if(sponsor != null) {
            return sponsor.getFullname();
        }
        return "";
    }

    @Override
    public int compareTo(Bill bill)
    {
        return this.getSenateBillNo().compareTo(bill.getSenateBillNo());
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










