package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.committee.CommitteeVersionIdView;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.model.bill.BillAmendment;

import java.util.stream.Collectors;

public class BillAmendmentView extends BillIdView {

    protected ListView<BillIdView> sameAs;
    protected String memo;
    protected String lawSection;
    protected String lawCode;
    protected String actClause;
    protected String fullText;
    protected CommitteeVersionIdView currentCommittee;
    protected ListView<SimpleMemberView> coSponsors;
    protected ListView<SimpleMemberView> multiSponsors;
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
                                    new CommitteeVersionIdView(billAmendment.getCurrentCommittee()) : null;
            this.coSponsors = ListView.of(billAmendment.getCoSponsors().stream()
                .map(SimpleMemberView::new)
                .collect(Collectors.toList()));
            this.multiSponsors = ListView.of(billAmendment.getMultiSponsors().stream()
                .map(SimpleMemberView::new)
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

    public CommitteeVersionIdView getCurrentCommittee() {
        return currentCommittee;
    }

    public ListView<SimpleMemberView> getCoSponsors() {
        return coSponsors;
    }

    public ListView<SimpleMemberView> getMultiSponsors() {
        return multiSponsors;
    }

    public boolean isUniBill() {
        return uniBill;
    }

    public boolean isStricken() {
        return isStricken;
    }
}
