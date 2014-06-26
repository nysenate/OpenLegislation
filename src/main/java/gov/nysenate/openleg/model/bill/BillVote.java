package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseLegContent;
import gov.nysenate.openleg.model.entity.Person;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillVote extends BaseLegContent
{
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private int voteType;

    private Date voteDate;

    public String oid;

    private List<String> ayes;

    private List<String> nays;

    private List<String> abstains;

    private List<String> absent;

    private List<String> excused;

    private Bill bill;

    private String billAmendment;

    private List<String> ayeswr;

    private String sequenceNumber;

    private String description = "";

    public final static int VOTE_TYPE_FLOOR = 1;

    public final static int VOTE_TYPE_COMMITTEE = 2;

    public int count() {
        return ayes.size() + nays.size() + abstains.size() + excused.size();
    }

    public BillVote() {
        super();
        ayes = new ArrayList<>();
        ayeswr = new ArrayList<>();
        nays = new ArrayList<>();
        abstains = new ArrayList<>();
        excused = new ArrayList<>();
        absent = new ArrayList<>();
    }

    public BillVote(String billId, String billAmendment, Date date, int type, String sequenceNumber) {
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

    public BillVote(BillAmendment amendment, Date date, int type, String sequenceNumber) {
        this();
        this.voteDate = date;
        this.setYear(new LocalDate(voteDate).getYear());
        this.setSession(this.getYear() % 2 == 0 ? this.getYear() - 1 : this.getYear());
        this.voteType = type;
        this.setSequenceNumber(sequenceNumber);
    }

    /** --- Basic Getters/Setters --- */

    public int getVoteType()
    {
        return voteType;
    }

    public String getOid()
    {
        return this.oid;
    }

    public void setOid(String oid)
    {
        this.oid = oid;
    }

    public Date getVoteDate()
    {
        return voteDate;
    }

    public List<String> getAyes()
    {
        return ayes;
    }

    public List<String> getNays()
    {
        return nays;
    }

    public List<String> getAbstains()
    {
        return abstains;
    }

    public List<String> getAbsent()
    {
        return absent;
    }

    public List<String> getExcused()
    {
        return excused;
    }

    public Bill getBill()
    {
        return bill;
    }

    public List<String> getAyeswr()
    {
        return ayeswr;
    }

    public String getDescription()
    {
        return description;
    }

    public void setVoteType(int voteType)
    {
        this.voteType = voteType;
    }

    public void setVoteDate(Date voteDate)
    {
        this.voteDate = voteDate;
    }

    public void setAyes(List<String> ayes)
    {
        this.ayes = ayes;
    }

    public void setNays(List<String> nays)
    {
        this.nays = nays;
    }

    public void setAbstains(List<String> abstains)
    {
        this.abstains = abstains;
    }

    public void setAbsent(List<String> absent)
    {
        this.absent = absent;
    }

    public void setExcused(List<String> excused)
    {
        this.excused = excused;
    }

    public void setBill(Bill bill)
    {
        this.bill = bill;
    }

    public void setAyeswr(List<String> ayeswr)
    {
        this.ayeswr = ayeswr;
    }

    public void setDescription(String description)
    {
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
    public boolean equals(Object obj)
    {
        if(obj != null && obj instanceof BillVote) {
            BillVote vote = (BillVote)obj;
            return this.oid.equals(vote.getOid());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getOid();
    }

    public String getSequenceNumber()
    {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber)
    {
        this.sequenceNumber = sequenceNumber;
    }

    public String getBillAmendment()
    {
        return billAmendment;
    }

    public void setBillAmendment(String billAmendment)
    {
        this.billAmendment = billAmendment;
    }
}
