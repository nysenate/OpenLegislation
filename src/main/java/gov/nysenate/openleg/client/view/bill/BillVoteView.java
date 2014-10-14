package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.*;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.entity.Member;

import java.util.stream.Collectors;

public class BillVoteView implements ViewObject
{
    protected BillIdView billId;
    protected String voteType;
    protected String voteDate;
    protected int sequenceNo;
    protected MapView<String, ListView<SimpleMemberView>> memberVotes;

    public BillVoteView(BillVote billVote) {
        if(billVote!=null) {
            this.billId = new BillIdView(billVote.getBillId());
            this.voteType = billVote.getVoteType().name();
            this.voteDate = billVote.getVoteDate().toString();
            this.sequenceNo = billVote.getSequenceNo();
            this.memberVotes = MapView.of(
                billVote.getMemberVotes().keySet().stream()
                    .collect(Collectors.toMap(BillVoteCode::name,
                        voteCode -> ListView.of(
                            billVote.getMembersByVote(voteCode).stream()
                                .map(m -> new SimpleMemberView(m))
                                .collect(Collectors.toList()))))
            );
        }
    }

    public BillIdView getBillId() {
        return billId;
    }

    public String getVoteType() {
        return voteType;
    }

    public String getVoteDate() {
        return voteDate;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public MapView<String, ListView<SimpleMemberView>> getMemberVotes() {
        return memberVotes;
    }

    @Override
    public String getViewType() {
        return "bill-vote";
    }
}
