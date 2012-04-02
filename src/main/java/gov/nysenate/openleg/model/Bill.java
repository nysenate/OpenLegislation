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

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("bill")
public class Bill extends SenateObject implements Comparable<Bill>  {

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

    protected int sortIndex = -1;

    @XStreamCollectionAlias(node="votes",value="vote")
    protected List<Vote> votes = new ArrayList<Vote>();

    @LuceneField
    protected Boolean stricken = false;

    public Boolean deleted = false;

    public Bill () {
        super();
    }

    @Override
    public int getYear() {
        return year;
    }



    public String getSenateBillNo() {
        return senateBillNo;
    }



    public String getTitle() {
        return title;
    }



    public String getLawSection() {
        return lawSection;
    }



    public String getSameAs() {
        return sameAs;
    }

    public List<String> getPreviousVersions() {
        return previousVersions;
    }

    public Person getSponsor() {
        return sponsor;
    }



    public List<Person> getCoSponsors() {
        return coSponsors;
    }

    public List<Person> getMultiSponsors() {
        return multiSponsors;
    }


    public String getSummary() {
        return summary;
    }


    public String getCurrentCommittee() {
        return currentCommittee;
    }


    public List<String> getPastCommittees() {
        return pastCommittees;
    }


    public List<Action> getActions() {
        return actions;
    }



    public String getFulltext() {
        return fulltext;
    }



    public String getMemo() {
        return memo;
    }



    public String getLaw() {
        return law;
    }



    public String getActClause() {
        return actClause;
    }



    public int getSortIndex() {
        return sortIndex;
    }



    public List<Vote> getVotes() {
        return votes;
    }



    public Boolean getStricken() {
        return stricken;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }



    public void setSenateBillNo(String senateBillNo) {
        this.senateBillNo = senateBillNo;
    }



    public void setTitle(String title) {
        this.title = title;
    }



    public void setLawSection(String lawSection) {
        this.lawSection = lawSection;
    }



    public void setSameAs(String sameAs) {
        this.sameAs = sameAs;
    }

    public void setPreviousVersions(List<String> previousVersions) {
        this.previousVersions = previousVersions;
    }

    public void setSponsor(Person sponsor) {
        this.sponsor = sponsor;
    }



    public void setCoSponsors(List<Person> coSponsors) {
        this.coSponsors = coSponsors;
    }


    public void setMultiSponsors(List<Person> multiSponsors) {
        this.multiSponsors = multiSponsors;
    }


    public void setSummary(String summary) {
        this.summary = summary;
    }



    public void setCurrentCommittee(String currentCommittee) {
        this.currentCommittee = currentCommittee;
    }


    public void setPastCommittees(List<String> pastCommittees) {
        if(pastCommittees == null) {
            this.pastCommittees = null;
            return;
        }

        this.pastCommittees = new ArrayList<String>();
        for(String pc:pastCommittees) {
            this.addPastCommittee(pc);
        }
    }



    public void setActions(List<Action> billEvents) {
        this.actions = billEvents;
    }



    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }



    public void setMemo(String memo) {
        this.memo = memo;
    }



    public void setLaw(String law) {
        this.law = law;
    }



    public void setActClause(String actClause) {
        this.actClause = actClause;
    }



    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }



    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }


    public void setStricken(boolean stricken) {
        this.stricken = stricken;
    }


    public void addPastCommittee(String committee) {
        if(pastCommittees == null)
            pastCommittees = new ArrayList<String>();

        if(!pastCommittees.contains(committee)) {
            pastCommittees.add(committee);
        }
    }


    public void addVote (Vote vote) {
        // Votes are unique by Bill + Date + Type + SequenceNumber (currently always 1)
        // Remove if present and add again to update
        votes.remove(vote);
        votes.add(vote);
    }

    public void removeVote(Vote vote) {
        if(votes == null) {
            return;
        }
        votes.remove(vote);
    }

    public void addPreviousVersion(String previousVersion) {
        if(previousVersions == null) {
            previousVersions = new ArrayList<String>();
        }
        if(!previousVersions.contains(previousVersion)) {
            previousVersions.add(previousVersion);
        }

    }

    public void addBillEvent(Action be) {
        if(this.actions == null)
            actions = new ArrayList<Action>();

        actions.add(be);
    }


    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Bill)
        {
            String d = getSenateBillNo();
            String thatId =  ((Bill)obj).getSenateBillNo();

            return (d.equals(thatId));
        }

        return false;
    }

    @Override
    public void merge(ISenateObject object) {
        if(!(object instanceof Bill)) {
            return;
        }
        Bill bill = (Bill)object;

        super.merge(object);

        if(!getSenateBillNo().equals(bill.getSenateBillNo())) {
            return;
        }

        if(year == 0) {
            year = bill.getYear();
        }

        if(title == null) {
            title = bill.getTitle();
        }
        else {
            if(bill.getTitle() != null && !bill.getTitle().equals("")) {
                title = bill.getTitle();
            }
        }

        if(lawSection == null) {
            lawSection = bill.getLawSection();
        }
        else {
            if(bill.getLawSection() != null && !bill.getLawSection().equals("")) {
                lawSection = bill.getLawSection();
            }
        }

        if(sameAs == null) {
            sameAs = bill.getSameAs();
        }
        else {
            if(bill.getSameAs() != null && !bill.getSameAs().equals("")) {
                sameAs = bill.getSameAs();
            }
        }

        if(sponsor == null) {
            sponsor = bill.getSponsor();
        }
        else {
            if(bill.getSponsor() != null && bill.getSponsor().getFullname() != null && !bill.getSponsor().getFullname().equals("")) {
                sponsor = bill.getSponsor();
            }
        }

        if(summary == null) {
            summary = bill.getSummary();
        }
        else {
            if(bill.getSummary() != null && !bill.getSummary().equals("")) {
                summary = bill.getSummary();
            }
        }

        if(currentCommittee == null) {
            currentCommittee = bill.getCurrentCommittee();
        }
        else {
            /*
             * current committee will only be updated when bill events are present
             */
            if(bill.getActions() != null && !bill.getActions().isEmpty())
                currentCommittee = bill.getCurrentCommittee();
        }

        if(bill.getFulltext() != null && bill.getFulltext().equals("*DELETE*")) {
            this.fulltext = null;
            bill.setFulltext(null);
        }
        if(fulltext == null || fulltext.equals("")) {
            fulltext = bill.getFulltext();
        }
        else {
            if(bill.getFulltext() != null && !bill.getFulltext().equals("")) {
                this.fulltext = bill.getFulltext();
            }
        }

        if(memo == null) {
            memo = bill.getMemo();
        }
        else {
            if(bill.getMemo() != null && !bill.getMemo().equals("")) {
                memo = bill.getMemo();
            }
        }

        if(law == null) {
            law = bill.getLaw();
        }
        else {
            if(bill.getLaw() != null && !bill.getLaw().equals("")) {
                law = bill.getLaw();
            }
        }

        if(actClause == null) {
            actClause = bill.getActClause();
        }
        else {
            if(bill.getActClause() != null && !bill.getActClause().equals("")) {
                actClause = bill.getActClause();
            }
        }

        if(sortIndex == -1) {
            sortIndex = bill.getSortIndex();
        }

        if(stricken == null) {
            stricken  = bill.getStricken();
        }
        else {
            if(bill.getStricken() != null) {
                stricken = bill.getStricken();
            }
        }

        if(bill.getPastCommittees() != null) {
            bill.getPastCommittees().remove("DELETED");
        }
        if(pastCommittees ==  null || pastCommittees.isEmpty()) {
            pastCommittees = bill.getPastCommittees();
        }
        else {
            if(bill.getPastCommittees() != null && !bill.getPastCommittees().isEmpty()) {
                this.pastCommittees = bill.getPastCommittees();
            }
        }

        if(actions == null || actions.isEmpty()) {
            actions = bill.getActions();
        }
        else {
            if(bill.getActions() != null && !bill.getActions().isEmpty()) {
                this.actions = bill.getActions();
            }
        }

        if(votes == null) {
            votes = bill.getVotes();
        }
        else {
            Vote newFloorVote = null;

            if(bill.getVotes() != null) {
                for(Vote vote:bill.getVotes()) {
                    if(vote.getVoteType() == Vote.VOTE_TYPE_FLOOR) {
                        newFloorVote = vote;
                        continue;
                    }

                    if(!this.votes.contains(vote)) {
                        this.votes.add(vote);
                    }
                    else {
                        this.votes.remove(vote);
                        this.votes.add(vote);
                    }
                }

                if(newFloorVote != null) {
                    for(int i = 0; i < this.getVotes().size(); i++) {
                        if(this.getVotes().get(i).getVoteType() == Vote.VOTE_TYPE_FLOOR)
                            this.votes.remove(i);
                    }
                    this.votes.add(newFloorVote);
                }
            }
        }

        if(coSponsors == null) {
            coSponsors = bill.getCoSponsors();
        }
        else {
            if(bill.getCoSponsors() != null && !bill.getCoSponsors().isEmpty()) {
                this.coSponsors = bill.getCoSponsors();
            }
        }

        if(multiSponsors == null) {
            multiSponsors = bill.getMultiSponsors();
        }
        else {
            if(bill.getMultiSponsors() != null && !bill.getMultiSponsors().isEmpty()) {
                this.multiSponsors = bill.getMultiSponsors();
            }
        }

        if(previousVersions == null) {
            previousVersions = bill.getPreviousVersions();
        }
        else {
            if(bill.getPreviousVersions() != null) {
                this.previousVersions = bill.getPreviousVersions();
            }
        }
    }

    @JsonIgnore
    @Override
    public String luceneOtype() {
        return "bill";
    }

    @JsonIgnore
    @Override
    public String luceneOid() {
        if (senateBillNo.indexOf("-" + year)==-1)
            return senateBillNo + "-" + year;
        else
            return senateBillNo;
    }

    @JsonIgnore
    @Override
    public HashMap<String,Fieldable> luceneFields() {
        HashMap<String,Fieldable> map = new HashMap<String,Fieldable>();

        if(this.getPastCommittees() != null) {
            String pcoms = "";
            for(String committee:pastCommittees) {
                pcoms += committee + ", ";
            }
            pcoms.replaceFirst(", $", "");
            map.put("pastcommittees", new Field("pastcommittees",pcoms, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

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
    public String luceneSummary() {
        return summary;
    }

    @JsonIgnore
    @Override
    public String luceneTitle() {
        return (title == null) ? summary : title;
    }

    @JsonIgnore
    @Override public String luceneOsearch() {
        return senateBillNo.split("-")[0] + " "
                + year + " "
                + senateBillNo + "-" + year
                + (sameAs != null ? " " + sameAs:"")
                + (sponsor != null ? " " + sponsor.getFullname():"")
                + (title != null ? " " + title:"")
                + (summary != null ? " " + summary:"");

    }

    @JsonIgnore
    public String getLuceneCoSponsors() {
        if(this.getCoSponsors() == null)
            return "";

        StringBuilder response = new StringBuilder();
        for( Person sponsor : coSponsors) {
            response.append(sponsor.getFullname() + ", ");
        }
        return response.toString().replaceAll(", $", "");
    }

    @JsonIgnore
    public String getLuceneMultiSponsors() {
        if(this.getMultiSponsors() == null)
            return "";

        StringBuilder response = new StringBuilder();
        for( Person sponsor : multiSponsors) {
            response.append(sponsor.getFullname() + ", ");
        }
        return response.toString().replaceAll(", $", "");
    }

    @JsonIgnore
    public String getLuceneActions() {
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
    public String getLuceneSponsor() {
        if(sponsor != null) {
            return sponsor.getFullname();
        }
        return "";
    }

    @Override
    public int compareTo(Bill bill) {
        return this.getSenateBillNo().compareTo(bill.getSenateBillNo());
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










