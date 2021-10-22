package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillVoteView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteBill;

public record AgendaVoteBillView(BillIdView bill, String action, CommitteeIdView referCommittee,
                                 BillVoteView vote, boolean isAmended) implements ViewObject {

    public AgendaVoteBillView(AgendaVoteBill voteBill) {
        this(new BillIdView(voteBill.getBillId()), voteBill.getVoteAction().name(),
                voteBill.getReferCommittee() != null ? new CommitteeIdView(voteBill.getReferCommittee()) : null,
        new BillVoteView(voteBill.getBillVote()), voteBill.isWithAmendment());
    }

    @Override
    public String getViewType() {
        return "agenda-vote-bill";
    }
}
