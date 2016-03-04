package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.SessionMember;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * The BillVote class is used to store vote information pertaining to a specific bill.
 * This model can be used for representing both floor and committee votes although
 * committee votes will have some extra metadata that should be tracked elsewhere.
 */
public class BillVote extends BaseLegislativeContent implements Serializable, Comparable<BillVote>
{
    private static final long serialVersionUID = -5265803060674818213L;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    /** Reference to the specific bill this vote was taken on. */
    private BillId billId;

    /** The type of bill vote (floor/committee) */
    private BillVoteType voteType;

    /** Date the vote was taken on. */
    private LocalDate voteDate;

    private CommitteeId committeeId;

    /** Sets of members grouped based upon how they voted. */
    @SuppressWarnings("serial")
    private SetMultimap<BillVoteCode, SessionMember> memberVotes = HashMultimap.create();

    /** An identifier to uniquely identify votes that came in on the same day.
     *  Currently not implemented as the source data does not contain this value. */
    private int sequenceNo;

    /** --- Constructors --- */

    public BillVote() {
        super();
    }

    public BillVote(BillVoteId billVoteId) {
        this(billVoteId.getBillId(), billVoteId.getVoteDate(), billVoteId.getVoteType(), billVoteId.getSequenceNo());
        this.committeeId = billVoteId.getCommitteeId();
    }

    public BillVote(BillId billId, LocalDate voteDate, BillVoteType type) {
        this(billId, voteDate, type, 1);
    }

    public BillVote(BillId billId, LocalDate voteDate, BillVoteType type, int sequenceNo) {
        this();
        this.billId = billId;
        this.voteDate = voteDate;
        this.setYear(voteDate.getYear());
        this.setSession(new SessionYear(this.getYear()));
        this.voteType = type;
        this.sequenceNo = sequenceNo;
    }

    public BillVote(BillId billId, LocalDate voteDate, BillVoteType type, int sequenceNo, CommitteeId committeeId) {
        this(billId, voteDate, type, sequenceNo);
        this.committeeId = committeeId;
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Creates and returns a unique id for the BillVote.
     */
    public BillVoteId getVoteId() {
        return new BillVoteId(this.billId, this.voteDate, this.voteType, this.sequenceNo, this.committeeId);
    }

    /**
     * Retrieve a set of members that voted as 'voteCode'.
     * @param voteCode BillVoteCode
     * @return Set<Member>
     */
    public Set<SessionMember> getMembersByVote(BillVoteCode voteCode) {
        return memberVotes.get(voteCode);
    }

    /**
     * Add a member to the map based on how they voted.
     * @param voteCode BillVoteCode
     * @param member Member
     */
    public void addMemberVote(BillVoteCode voteCode, SessionMember member) {
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
     * Ignore the parent class (session/modified/published date) during equality checking.
     * The vote date and type should suffice in uniquely identifying the vote for a certain billId.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BillVote other = (BillVote) obj;
        return Objects.equals(this.billId, other.billId) &&
               Objects.equals(this.voteType, other.voteType) &&
               Objects.equals(this.voteDate, other.voteDate) &&
               Objects.equals(this.memberVotes, other.memberVotes) &&
               Objects.equals(this.sequenceNo, other.sequenceNo) &&
               Objects.equals(this.committeeId, other.committeeId);
    }

    @Override
    public int compareTo(BillVote o) {
        return ComparisonChain.start()
            .compare(this.getVoteDate(), o.getVoteDate())
            .compare(this.getVoteType().code, o.getVoteType().code)
            .result();
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId, voteType, voteDate, memberVotes, sequenceNo);
    }

    @Override
    public String toString() {
        return "BillVote{" +
                "sequenceNo=" + sequenceNo +
                ", billId=" + billId +
                ", voteType=" + voteType +
                ", voteDate=" + voteDate +
                ", committeeId=" + committeeId +
                '}';
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

    public LocalDate getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(LocalDate voteDate) {
        this.voteDate = voteDate;
    }

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public SetMultimap<BillVoteCode, SessionMember> getMemberVotes() {
        return memberVotes;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(CommitteeId committeeId) {
        this.committeeId = committeeId;
    }
}