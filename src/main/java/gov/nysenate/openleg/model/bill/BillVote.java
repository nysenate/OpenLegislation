package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseLegislativeContent;
import gov.nysenate.openleg.model.entity.Member;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillVote extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = -5265803060674818213L;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    /** The type of bill vote (floor/committee) */
    private BillVoteType voteType;

    /** Date the vote was taken on. */
    private Date voteDate;

    /** Reference to the specific bill this vote was taken on. */
    private BillId billId;

    /** Members that voted 'Yes'. */
    private Set<Member> ayes = new LinkedHashSet<>();

    /** Members that voted 'No'. */
    private Set<Member> nays = new LinkedHashSet<>();

    /** Members that abstained from vote. */
    private Set<Member> abstains = new LinkedHashSet<>();

    /** Members that were absent for the vote. */
    private Set<Member> absent = new LinkedHashSet<>();

    /** Members that were excused from the vote. */
    private Set<Member> excused = new LinkedHashSet<>();

    /** Members that voted 'Yes with reservations'. */
    private Set<Member> ayeswr = new LinkedHashSet<>();

    /** An identifier to uniquely identify votes that came in on the same day.
     *  Currently not implemented as the source data does not contain this value. */
    private int sequenceNumber;

    /** --- Constructors --- */

    public BillVote() {
        super();
    }

    public BillVote(BillId billId, Date date, BillVoteType type, int sequenceNumber) {
        this();
        this.billId = billId;
        this.voteDate = date;
        this.setYear(new LocalDate(date).getYear());
        this.setSession(resolveSessionYear(this.getYear()));
        this.voteType = type;
        this.sequenceNumber = sequenceNumber;
    }

    /** --- Functional Getters/Setters --- */

    /** Creates and returns a unique id for the BillVote */
    public String getVoteId() {
        return this.billId.toString() + ":" + this.voteType + ":" + this.voteDate + ":" + this.sequenceNumber;
    }

    public int count() {
        return ayes.size() + nays.size() + abstains.size() + excused.size();
    }

    public void addAye(Member member) {
        ayes.add(member);
    }

    public void addAyeWR(Member member) {
        ayeswr.add(member);
    }

    public void addNay(Member member) {
        nays.add(member);
    }

    public void addAbstain(Member member) {
        abstains.add(member);
    }

    public void addAbsent(Member member) {
        absent.add(member);
    }

    public void addExcused(Member member) {
        excused.add(member);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillVote)) return false;
        BillVote billVote = (BillVote) o;
        if (sequenceNumber != billVote.sequenceNumber) return false;
        if (absent != null ? !absent.equals(billVote.absent) : billVote.absent != null) return false;
        if (abstains != null ? !abstains.equals(billVote.abstains) : billVote.abstains != null) return false;
        if (ayes != null ? !ayes.equals(billVote.ayes) : billVote.ayes != null) return false;
        if (ayeswr != null ? !ayeswr.equals(billVote.ayeswr) : billVote.ayeswr != null) return false;
        if (billId != null ? !billId.equals(billVote.billId) : billVote.billId != null) return false;
        if (excused != null ? !excused.equals(billVote.excused) : billVote.excused != null) return false;
        if (nays != null ? !nays.equals(billVote.nays) : billVote.nays != null) return false;
        if (voteDate != null ? !voteDate.equals(billVote.voteDate) : billVote.voteDate != null) return false;
        if (voteType != billVote.voteType) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = voteType != null ? voteType.hashCode() : 0;
        result = 31 * result + (voteDate != null ? voteDate.hashCode() : 0);
        result = 31 * result + (billId != null ? billId.hashCode() : 0);
        result = 31 * result + (ayes != null ? ayes.hashCode() : 0);
        result = 31 * result + (nays != null ? nays.hashCode() : 0);
        result = 31 * result + (abstains != null ? abstains.hashCode() : 0);
        result = 31 * result + (absent != null ? absent.hashCode() : 0);
        result = 31 * result + (excused != null ? excused.hashCode() : 0);
        result = 31 * result + (ayeswr != null ? ayeswr.hashCode() : 0);
        result = 31 * result + sequenceNumber;
        return result;
    }

    /** --- Basic Getters/Setters --- */

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public static void setDateFormat(SimpleDateFormat dateFormat) {
        BillVote.dateFormat = dateFormat;
    }

    public BillVoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(BillVoteType voteType) {
        this.voteType = voteType;
    }

    public Date getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(Date voteDate) {
        this.voteDate = voteDate;
    }

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public Set<Member> getAyes() {
        return ayes;
    }

    public void setAyes(Set<Member> ayes) {
        this.ayes = ayes;
    }

    public Set<Member> getNays() {
        return nays;
    }

    public void setNays(Set<Member> nays) {
        this.nays = nays;
    }

    public Set<Member> getAbstains() {
        return abstains;
    }

    public void setAbstains(Set<Member> abstains) {
        this.abstains = abstains;
    }

    public Set<Member> getAbsent() {
        return absent;
    }

    public void setAbsent(Set<Member> absent) {
        this.absent = absent;
    }

    public Set<Member> getExcused() {
        return excused;
    }

    public void setExcused(Set<Member> excused) {
        this.excused = excused;
    }

    public Set<Member> getAyeswr() {
        return ayeswr;
    }

    public void setAyeswr(Set<Member> ayeswr) {
        this.ayeswr = ayeswr;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
