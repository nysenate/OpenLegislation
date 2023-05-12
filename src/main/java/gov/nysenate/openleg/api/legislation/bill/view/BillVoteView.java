package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.attendance.SenateVoteAttendanceView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.bill.BillVote;
import gov.nysenate.openleg.legislation.bill.BillVoteCode;
import gov.nysenate.openleg.legislation.bill.BillVoteType;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;

public class BillVoteView implements ViewObject
{
    protected BillIdView billId;
    protected String version;
    protected BillVoteType voteType;
    protected LocalDate voteDate;
    protected int sequenceNo;
    protected CommitteeIdView committee;
    protected MapView<String, ListView<MemberView>> memberVotes;
    protected SenateVoteAttendanceView attendance;

    public BillVoteView(BillVote billVote) {
        if(billVote != null) {
            this.billId = new BillIdView(billVote.getBillId());
            this.version = new BillIdView(billVote.getBillId()).getVersion();
            this.voteType = billVote.getVoteType();
            this.voteDate = billVote.getVoteDate();
            this.sequenceNo = billVote.getSequenceNo();
            this.committee = billVote.getCommitteeId() != null ? new CommitteeIdView(billVote.getCommitteeId()) : null;
            this.memberVotes = MapView.of(
                billVote.getMemberVotes().keySet().stream()
                    .collect(Collectors.toMap(BillVoteCode::name, voteCode ->
                        ListView.of(billVote.getMembersByVote(voteCode).stream()
                            .map(MemberView::new)
                            .sorted(Comparator.comparing(MemberView::getShortName))
                            .collect(Collectors.toList()))))
            );
            this.attendance = new SenateVoteAttendanceView(billVote.getAttendance());
        }
    }
    public BillVoteView(){

    }

    public BillIdView getBillId() {
        return billId;
    }

    public String getVersion() {
        return version;
    }

    public void setVoteDate(String date){
        voteDate = LocalDate.parse(date);
    }

    public BillVoteType getVoteType() {
        return voteType;
    }

    public LocalDate getVoteDate() {
        return voteDate;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public CommitteeIdView getCommittee() {
        return committee;
    }

    public MapView<String, ListView<MemberView>> getMemberVotes() {
        return memberVotes;
    }

    public SenateVoteAttendanceView getAttendance() {
        return attendance;
    }

    @Override
    public String getViewType() {
        return "bill-vote";
    }
}