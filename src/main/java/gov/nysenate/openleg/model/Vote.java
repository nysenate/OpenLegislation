package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.xstream.XStreamCollectionAlias;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("vote")
public class Vote extends SenateObject {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private int voteType;

    private String id;

    private Date voteDate;

    @XStreamCollectionAlias(node="ayes",value="member")
    private List<String> ayes;

    @XStreamCollectionAlias(node="nays",value="member")
    private List<String> nays;

    @XStreamCollectionAlias(node="abstains",value="member")
    private List<String> abstains;

    @XStreamCollectionAlias(node="absent",value="member")
    private List<String> absent;

    @XStreamCollectionAlias(node="excused",value="member")
    private List<String> excused;

    @JsonIgnore
    private Bill bill;

    @XStreamCollectionAlias(node="ayeswr",value="member")
    private List<String> ayeswr;

    private String description = "";

    public final static int VOTE_TYPE_FLOOR = 1;

    public final static int VOTE_TYPE_COMMITTEE = 2;

    public int count() {
        return ayes.size()+nays.size()+abstains.size()+excused.size();
    }

    public Vote() {
        super();
        ayes = new ArrayList<String>();
        ayeswr = new ArrayList<String>();
        nays = new ArrayList<String>();
        abstains = new ArrayList<String>();
        excused = new ArrayList<String>();
        absent = new ArrayList<String>();
    }

    public Vote (Bill bill, Date date, int type, String sequenceNumber) {
        this();
        this.bill = bill;
        this.voteDate = date;
        this.voteType = type;
        this.id = buildId(bill, date, sequenceNumber);
    }


    public String buildId (Bill bill, Date voteDate, String sequenceNumber)
    {
        return bill.getSenateBillNo()+'-'+dateFormat.format(voteDate)+'-'+String.valueOf(voteType)+'-'+sequenceNumber;
    }

    public Vote (Bill bill, Date voteDate, int ayeCount, int nayCount)
    {
        this();
        this.id = buildId(bill, voteDate, "1");
        this.bill = bill;
        this.voteDate = voteDate;

    }

    public int getVoteType() {
        return voteType;
    }



    public String getId() {
        return id;
    }



    public Date getVoteDate() {
        return voteDate;
    }



    public List<String> getAyes() {
        return ayes;
    }



    public List<String> getNays() {
        return nays;
    }



    public List<String> getAbstains() {
        return abstains;
    }

    public List<String> getAbsent() {
        return absent;
    }

    public List<String> getExcused() {
        return excused;
    }


    @JsonIgnore
    public Bill getBill() {
        return bill;
    }



    public List<String> getAyeswr() {
        return ayeswr;
    }



    public String getDescription() {
        return description;
    }



    public static int getVoteTypeFloor() {
        return VOTE_TYPE_FLOOR;
    }



    public static int getVoteTypeCommittee() {
        return VOTE_TYPE_COMMITTEE;
    }



    public void setVoteType(int voteType) {
        this.voteType = voteType;
    }



    public void setId(String id) {
        this.id = id;
    }



    public void setVoteDate(Date voteDate) {
        this.voteDate = voteDate;
    }



    public void setAyes(List<String> ayes) {
        this.ayes = ayes;
    }



    public void setNays(List<String> nays) {
        this.nays = nays;
    }



    public void setAbstains(List<String> abstains) {
        this.abstains = abstains;
    }

    public void setAbsent(List<String> absent) {
        this.absent = absent;
    }

    public void setExcused(List<String> excused) {
        this.excused = excused;
    }



    public void setBill(Bill bill) {
        this.bill = bill;
    }



    public void setAyeswr(List<String> ayeswr) {
        this.ayeswr = ayeswr;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public void addAye(Person person)
    {
        ayes.add(person.getFullname());
    }


    public void addAyeWR(Person person)
    {
        ayeswr.add(person.getFullname());
    }

    public void addNay(Person person)
    {
        nays.add(person.getFullname());
    }

    public void addAbstain(Person person)
    {
        abstains.add(person.getFullname());
    }

    public void addAbsent(Person person)
    {
        absent.add(person.getFullname());
    }

    public void addExcused(Person person)
    {
        excused.add(person.getFullname());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Vote) {
            Vote vote = (Vote)obj;
            return this.id.equals(vote.getId());
        }
        return false;
    }

    @JsonIgnore
    @Override
    public HashMap<String, Fieldable> luceneFields() {
        HashMap<String,Fieldable> map = new HashMap<String,Fieldable>();

        if (bill != null) {
            map.put("billno", new Field("billno",bill.getSenateBillNo(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
            map.put("otherSponsors", new Field("otherSponsors",StringUtils.join(bill.getOtherSponsors(),", "), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
            map.put("sponsor", new Field("sponsor", bill.getSponsor().getFullname(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        switch(voteType) {
        case Vote.VOTE_TYPE_COMMITTEE:
            if(description !=null)
                map.put("committee", new Field("committee",description, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
            else if (bill != null)
                map.put("committee", new Field("committee",bill.getCurrentCommittee(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        Iterator<String> itVote = null;
        StringBuilder sbVotes = null;

        if (abstains != null) {
            sbVotes = new StringBuilder();
            itVote = abstains.iterator();
            while (itVote.hasNext()) {
                sbVotes.append(itVote.next()).append(" ");
            }

            map.put("abstain", new Field("abstain",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        if (ayes != null) {
            sbVotes = new StringBuilder();
            itVote = ayes.iterator();
            while (itVote.hasNext()) {
                sbVotes.append(itVote.next()).append(" ");
            }

            map.put("aye", new Field("aye",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        if (excused != null) {

            sbVotes = new StringBuilder();
            itVote = excused.iterator();
            while (itVote.hasNext()) {
                sbVotes.append(itVote.next()).append(" ");
            }

            map.put("excused", new Field("excused",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        if (nays != null) {

            sbVotes = new StringBuilder();
            itVote = nays.iterator();
            while (itVote.hasNext()) {
                sbVotes.append(itVote.next()).append(" ");
            }

            map.put("nay", new Field("nay",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        map.put("when", new Field("when",voteDate.getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        return map;
    }

    @JsonIgnore
    @Override
    public String luceneOid() {
        return id;
    }

    @JsonIgnore
    @Override
    public String luceneOsearch() {

        if (bill == null)
            return "";

        StringBuilder oSearch = new StringBuilder("");
        oSearch.append(bill.getSenateBillNo() + " ");
        switch(voteType) {
        case Vote.VOTE_TYPE_COMMITTEE:
            oSearch.append(" Committee Vote ");
            oSearch.append(bill.getCurrentCommittee());
        case Vote.VOTE_TYPE_FLOOR:
            oSearch.append(" Floor Vote ");
        }
        return oSearch.toString();
    }

    @JsonIgnore
    @Override
    public String luceneOtype() {
        return "vote";
    }

    @JsonIgnore
    @Override
    public String luceneSummary() {
        return java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(voteDate);
    }

    @JsonIgnore
    @Override
    public String luceneTitle() {

        String title = "";

        if (bill != null)
            title += bill.getSenateBillNo();

        title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(voteDate);

        switch(voteType) {
        case Vote.VOTE_TYPE_COMMITTEE:
            return title + " - Committee Vote";
        case Vote.VOTE_TYPE_FLOOR:
            return title + " - Floor Vote";
        }
        return title;
    }

    @SuppressWarnings("deprecation")
    @JsonIgnore
    @Override
    public int getYear() {
        if(bill != null) {
            return bill.getYear();
        }
        if(voteDate != null) {
            return voteDate.getYear();
        }
        return 9999;
    }

    @Override
    public void merge(ISenateObject obj) {
        return;
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
