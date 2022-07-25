package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillVoteView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteBill;

public record AgendaVoteBillView(BillIdView bill, String action, boolean amended, CommitteeIdView referCommittee,
                                 BillVoteView vote) implements ViewObject {

    public AgendaVoteBillView(AgendaVoteBill voteBill) {
        this(new BillIdView(voteBill.getBillId()), voteBill.voteAction().name(),
                voteBill.isWithAmendment(),
                voteBill.referCommittee() != null ? new CommitteeIdView(voteBill.referCommittee()) : null,
                new BillVoteView(voteBill.billVote()));
    }

    @Override
    public String getViewType() {
        return "agenda-vote-bill";
    }
}
