package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteCode;

import java.util.stream.Collectors;

public class BillVoteView implements ViewObject
{
    protected String version;
    protected String voteType;
    protected String voteDate;
    protected CommitteeIdView committee;
    protected MapView<String, ListView<MemberView>> memberVotes;

    public BillVoteView(BillVote billVote) {
        if(billVote != null) {
            this.version = new BillIdView(billVote.getBillId()).getVersion();
            this.voteType = billVote.getVoteType().name();
            this.voteDate = billVote.getVoteDate().toString();
            this.committee = billVote.getCommitteeId() != null ? new CommitteeIdView(billVote.getCommitteeId()) : null;
            this.memberVotes = MapView.of(
                billVote.getMemberVotes().keySet().stream()
                    .collect(Collectors.toMap(BillVoteCode::name, voteCode ->
                        ListView.of(billVote.getMembersByVote(voteCode).stream()
                            .map(m -> new MemberView(m))
                            .sorted((o1, o2) -> o1.getShortName().compareTo(o2.getShortName()))
                            .collect(Collectors.toList()))))
            );
        }
    }

    public String getVersion() {
        return version;
    }

    public String getVoteType() {
        return voteType;
    }

    public String getVoteDate() {
        return voteDate;
    }

    public CommitteeIdView getCommittee() {
        return committee;
    }

    public MapView<String, ListView<MemberView>> getMemberVotes() {
        return memberVotes;
    }

    @Override
    public String getViewType() {
        return "bill-vote";
    }
}