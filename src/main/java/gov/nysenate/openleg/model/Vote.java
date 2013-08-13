package gov.nysenate.openleg.model;

import gov.nysenate.openleg.xstream.XStreamCollectionAlias;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("vote")
public class Vote extends BaseObject {

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

    public Vote (Bill bill, Date voteDate, int ayeCount, int nayCount)
    {
        this();
        this.id = buildId(bill, voteDate, "1");
        this.bill = bill;
        this.voteDate = voteDate;
    }

    public String buildId (Bill bill, Date voteDate, String sequenceNumber)
    {
        return bill.getBillId()+'-'+dateFormat.format(voteDate)+'-'+String.valueOf(voteType)+'-'+sequenceNumber;
    }

    public int getVoteType() {
        return voteType;
    }

    /**
     * The object type of the bill.
     */
    @JsonIgnore
    public String getOtype()
    {
        return "vote";
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public String getOid()
    {
        return this.getId();
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


    public Bill getBill() {
        return bill;
    }



    public List<String> getAyeswr() {
        return ayeswr;
    }



    public String getDescription() {
        return description;
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
    public int getYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(voteDate);
        return cal.get(java.util.Calendar.YEAR);
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
