package gov.nysenate.openleg.model.bill;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;
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

    /** Sets of members grouped based upon how they voted. */
    @SuppressWarnings("serial")
    private SetMultimap<BillVoteCode, Member> memberVotes = HashMultimap.create();

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

    /**
     * Creates and returns a unique id for the BillVote.
     */
    public BillVoteId getVoteId() {
        return new BillVoteId(this.billId, this.voteDate, this.voteType, this.sequenceNumber);
    }

    /**
     * Retrieve a set of members that voted as 'voteCode'.
     * @param voteCode BillVoteCode
     * @return Set<Member>
     */
    public Set<Member> getMembersByVote(BillVoteCode voteCode) {
        return memberVotes.get(voteCode);
    }

    /**
     * Add a member to the map based on how they voted.
     * @param voteCode BillVoteCode
     * @param member Member
     */
    public void addMemberVote(BillVoteCode voteCode, Member member) {
        memberVotes.put(voteCode, member);
    }

    /**
     * Returns a count of all the members that have been added to the voting roll.
     */
    public int count() {
        return memberVotes.size();
    }

    /** --- Overrides --- */

    /**
     * Ignores the parent class during equality checking.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillVote)) return false;
        BillVote billVote = (BillVote) o;
        if (sequenceNumber != billVote.sequenceNumber) return false;
        if (billId != null ? !billId.equals(billVote.billId) : billVote.billId != null) return false;
        if (memberVotes != null ? !memberVotes.equals(billVote.memberVotes) : billVote.memberVotes != null)
            return false;
        if (voteDate != null ? !voteDate.equals(billVote.voteDate) : billVote.voteDate != null) return false;
        if (voteType != billVote.voteType) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (voteType != null ? voteType.hashCode() : 0);
        result = 31 * result + (voteDate != null ? voteDate.hashCode() : 0);
        result = 31 * result + (billId != null ? billId.hashCode() : 0);
        result = 31 * result + (memberVotes != null ? memberVotes.hashCode() : 0);
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

    public SetMultimap<BillVoteCode, Member> getMemberVotes() {
        return memberVotes;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}