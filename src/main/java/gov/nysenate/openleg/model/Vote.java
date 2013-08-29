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

    private Date voteDate;

    public String oid;

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

    @JsonIgnore
    private String sequenceNumber;

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

    public Vote(String billId, Date date, int type, String sequenceNumber)
    {
        this();
        this.voteDate = date;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(voteDate);
        this.setYear(cal.get(java.util.Calendar.YEAR));
        this.setSession(this.getYear() % 2 == 0 ? this.getYear() -1 : this.getYear());
        this.voteType = type;
        this.setSequenceNumber(sequenceNumber);
        this.oid = billId+'-'+dateFormat.format(voteDate)+'-'+String.valueOf(voteType)+'-'+sequenceNumber;
    }

    public Vote(Bill bill, Date date, int type, String sequenceNumber) {
        this(bill.getBillId(), date, type, sequenceNumber);
        this.bill = bill;
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

    @JsonIgnore
    public String getOid()
    {
        return this.oid;
    }

    public void setOid(String oid)
    {
        this.oid = oid;
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
            return this.oid.equals(vote.getOid());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getOid();
    }

    @Deprecated
    public String getId() {
        return oid;
    }

    @Deprecated
    public void setId(String oid) {
        this.oid = oid;
    }

    @JsonIgnore
    public String getSequenceNumber()
    {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber)
    {
        this.sequenceNumber = sequenceNumber;
    }
}
