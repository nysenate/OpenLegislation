package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.entity.Member;

import java.util.stream.Collectors;

public class BillAmendmentView extends BillIdView {

    protected ListView<BillIdView> sameAs;
    protected String memo;
    protected String lawSection;
    protected String lawCode;
    protected String actClause;
    protected String fullText;
    protected String currentCommittee;
    protected String currentCommitteeDate;
    protected ListView<String> coSponsors;
    protected ListView<String> multiSponsors;
    protected ListView<BillVoteView> votes;
    protected boolean uniBill;
    protected boolean isStricken;

    public BillAmendmentView(BillAmendment billAmendment) {
        super(billAmendment != null ? billAmendment.getBillId() : null);
        if (billAmendment != null) {
            this.sameAs = ListView.of(billAmendment.getSameAs().stream()
                    .map(BillIdView::new)
                    .collect(Collectors.toList()));
            this.memo = billAmendment.getMemo();
            this.lawSection = billAmendment.getLawSection();
            this.lawCode = billAmendment.getLaw();
            this.actClause = billAmendment.getActClause();
            this.fullText = billAmendment.getFullText();
            this.currentCommittee = billAmendment.getCurrentCommittee() != null ?
                    billAmendment.getCurrentCommittee().getName() : null;
            this.currentCommitteeDate = billAmendment.getCurrentCommittee() != null ?
                    billAmendment.getCurrentCommittee().getReferenceDate().toString() : null;
            this.coSponsors = ListView.ofStringList(billAmendment.getCoSponsors().stream()
                    .map(Member::getLbdcShortName)
                    .collect(Collectors.toList()));
            this.multiSponsors = ListView.ofStringList(billAmendment.getMultiSponsors().stream()
                    .map(Member::getLbdcShortName)
                    .collect(Collectors.toList()));
            this.votes = ListView.of(billAmendment.getVotesList().stream()
                    .map(BillVoteView::new)
                    .collect(Collectors.toList()));
            this.uniBill = billAmendment.isUniBill();
            this.isStricken = billAmendment.isStricken();
        }
    }

    @Override
    public String getViewType() {
        return "bill-amendment";
    }

    public ListView<BillIdView> getSameAs() {
        return sameAs;
    }

    public String getMemo() {
        return memo;
    }

    public String getLawSection() {
        return lawSection;
    }

    public String getLawCode() {
        return lawCode;
    }

    public String getActClause() {
        return actClause;
    }

    public String getFullText() {
        return fullText;
    }

    public String getCurrentCommittee() {
        return currentCommittee;
    }

    public String getCurrentCommitteeDate() {
        return currentCommitteeDate;
    }

    public ListView<String> getCoSponsors() {
        return coSponsors;
    }

    public ListView<String> getMultiSponsors() {
        return multiSponsors;
    }

    public ListView<BillVoteView> getVotes() {
        return votes;
    }

    public boolean isUniBill() {
        return uniBill;
    }

    public boolean isStricken() {
        return isStricken;
    }
}
