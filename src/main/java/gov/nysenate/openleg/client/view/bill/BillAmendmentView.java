package gov.nysenate.openleg.client.view.bill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.util.BillTextUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BillAmendmentView extends BillIdView
{
    protected LocalDate publishDate;
    protected ListView<BillIdView> sameAs;
    protected String memo;
    protected String lawSection;
    protected String lawCode;
    protected String actClause;
    protected List<BillTextFormat> fullTextFormats;
    protected String fullText = "";
    protected String fullTextHtml = "";
    protected String fullTextTemplate = "";
    protected ListView<MemberView> coSponsors;
    protected ListView<MemberView> multiSponsors;
    protected boolean uniBill;
    protected boolean isStricken;

    public BillAmendmentView(){}

    public BillAmendmentView(BillAmendment billAmendment, PublishStatus publishStatus, Set<BillTextFormat> fullTextFormats) {
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
            this.fullTextFormats = new ArrayList<>(fullTextFormats);
            if (this.fullTextFormats.contains(BillTextFormat.PLAIN)) {
                this.fullText = BillTextUtils.getPlainTextWithoutLineNumbers(billAmendment);
            }
            if (this.fullTextFormats.contains(BillTextFormat.HTML)) {
                this.fullTextHtml = billAmendment.getFullText(BillTextFormat.HTML);
            }
            if (this.fullTextFormats.contains(BillTextFormat.TEMPLATE)) {
                this.fullTextTemplate = billAmendment.getFullText(BillTextFormat.TEMPLATE);
            }
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

    public void setPublishDate(String date){
        publishDate = LocalDate.parse(date);
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

    public List<BillTextFormat> getFullTextFormats() {
        return fullTextFormats;
    }

    public String getFullTextHtml() {
        return fullTextHtml;
    }

    public String getFullTextTemplate() {
        return fullTextTemplate;
    }
}
