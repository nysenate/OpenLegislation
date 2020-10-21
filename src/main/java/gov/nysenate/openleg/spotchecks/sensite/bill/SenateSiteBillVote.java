package gov.nysenate.openleg.spotchecks.sensite.bill;

import com.google.common.collect.*;
import gov.nysenate.openleg.api.legislation.bill.view.BillVoteView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillVoteCode;
import gov.nysenate.openleg.legislation.bill.BillVoteId;
import gov.nysenate.openleg.legislation.bill.BillVoteType;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static gov.nysenate.openleg.legislation.committee.Chamber.SENATE;

/**
 * Data model for a nysenate.gov bill vote.
 */
public class SenateSiteBillVote implements Comparable<SenateSiteBillVote> {

    private BillId billId;
    private BillVoteType voteType;
    private LocalDate voteDate;
    private String committeeName;
    private ImmutableMap<BillVoteCode, Integer> voteCounts;
    private ImmutableMultimap<BillVoteCode, Integer> voteRoll;

    /**
     * Constructs a senate site bill vote from a bill vote view.
     * Useful for comparison.
     * @param voteView
     */
    public SenateSiteBillVote(BillVoteView voteView) {
        this.billId = voteView.getBillId().toBillId();
        this.voteType = voteView.getVoteType();
        this.voteDate = voteView.getVoteDate();
        this.committeeName = Optional.ofNullable(voteView.getCommittee())
                .map(CommitteeIdView::getName)
                .orElse(null);
        ImmutableMap.Builder<BillVoteCode, Integer> voteCountBuilder = ImmutableMap.builder();
        ImmutableMultimap.Builder<BillVoteCode, Integer> voteRollBuilder = ImmutableMultimap.builder();
        voteView.getMemberVotes().getItems().forEach((codeStr, memberListView) -> {
            BillVoteCode voteCode = BillVoteCode.getValue(codeStr);
            ImmutableList<MemberView> members = memberListView.getItems();
            voteCountBuilder.put(voteCode, members.size());
            members.stream()
                    .map(MemberView::getMemberId)
                    .forEach(id -> voteRollBuilder.put(voteCode, id));
        });
        voteCounts = voteCountBuilder.build();
        voteRoll = voteRollBuilder.build();
    }

    public SenateSiteBillVote(BillId billId, BillVoteType voteType, LocalDate voteDate, String committeeName,
                              Map<BillVoteCode, Integer> voteCounts,
                              Multimap<BillVoteCode, Integer> voteRoll) {
        this.billId = billId;
        this.voteType = voteType;
        this.voteDate = voteDate;
        this.committeeName = committeeName;
        this.voteCounts = ImmutableMap.copyOf(voteCounts);
        this.voteRoll = ImmutableMultimap.copyOf(voteRoll);
    }

    @Override
    public int compareTo(SenateSiteBillVote o) {
        return ComparisonChain.start()
                .compare(this.getVoteId(), o.getVoteId())
                .result();
    }

    public BillVoteId getVoteId() {
        return new BillVoteId(
                billId,
                voteDate,
                voteType,
                1,  // Fixme: seqNo has never been any value other than 1, but still...
                committeeName != null ? new CommitteeId(SENATE, committeeName) : null
        );
    }

    public BillId getBillId() {
        return billId;
    }

    public BillVoteType getVoteType() {
        return voteType;
    }

    public LocalDate getVoteDate() {
        return voteDate;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public ImmutableMap<BillVoteCode, Integer> getVoteCounts() {
        return voteCounts;
    }

    public ImmutableMultimap<BillVoteCode, Integer> getVoteRoll() {
        return voteRoll;
    }
}
