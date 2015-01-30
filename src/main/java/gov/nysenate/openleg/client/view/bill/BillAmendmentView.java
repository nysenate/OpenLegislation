package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.committee.CommitteeVersionIdView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.util.BillTextUtils;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class BillAmendmentView extends BillIdView
{
    protected LocalDate publishDate;
    protected ListView<BillIdView> sameAs;
    protected String memo;
    protected String lawSection;
    protected String lawCode;
    protected String actClause;
    protected String fullText;
    protected ListView<MemberView> coSponsors;
    protected ListView<MemberView> multiSponsors;
    protected boolean uniBill;
    protected boolean isStricken;

    public BillAmendmentView(BillAmendment billAmendment, PublishStatus publishStatus) {
        super(billAmendment != null ? billAmendment.getBillId() : null);
        if (billAmendment != null) {
            this.publishDate = publishStatus.getEffectDateTime().toLocalDate();
            this.sameAs = ListView.of(billAmendment.getSameAs().stream()
                .map(BillIdView::new)
                .collect(Collectors.toList()));
            this.memo = billAmendment.getMemo();
            this.lawSection = billAmendment.getLawSection();
            this.lawCode = billAmendment.getLaw();
            this.actClause = billAmendment.getActClause();
            this.fullText = BillTextUtils.formatBillText(billAmendment.isResolution(), billAmendment.getFullText());
            this.coSponsors = ListView.of(billAmendment.getCoSponsors().stream()
                .map(MemberView::new)
                .collect(Collectors.toList()));
            this.multiSponsors = ListView.of(billAmendment.getMultiSponsors().stream()
                .map(MemberView::new)
                .collect(Collectors.toList()));
            this.uniBill = billAmendment.isUniBill();
            this.isStricken = billAmendment.isStricken();
        }
    }

    @Override
    public String getViewType() {
        return "bill-amendment";
    }

    public LocalDate getPublishDate() {
        return publishDate;
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

    public ListView<MemberView> getCoSponsors() {
        return coSponsors;
    }

    public ListView<MemberView> getMultiSponsors() {
        return multiSponsors;
    }

    public boolean isUniBill() {
        return uniBill;
    }

    public boolean isStricken() {
        return isStricken;
    }
}
