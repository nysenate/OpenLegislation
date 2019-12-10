package gov.nysenate.openleg.client.view.bill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.util.BillTextUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.bill.BillTextFormat.*;

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
    protected String fullText;
    protected String fullTextHtml;
    protected String fullTextHtml5;
    protected List<BillTextUtils.TextDiff> fullTextDiff;
    protected ListView<MemberView> coSponsors;
    protected ListView<MemberView> multiSponsors;
    protected boolean uniBill;
    protected boolean isStricken;

    protected BillAmendmentView(){}

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
            this.fullTextFormats = new ArrayList<>(billAmendment.getFullTextFormats());
            this.fullText = BillTextUtils.formatBillText(billAmendment.isResolution(), billAmendment.getFullText(PLAIN));
            this.fullTextHtml = billAmendment.getFullText(HTML);
            this.fullTextHtml5 = billAmendment.getFullText(HTML5);
            try {
                BillTextUtils.TextDiff[] textDiffArray = new ObjectMapper().readValue(billAmendment.getFullText(DIFF), BillTextUtils.TextDiff[].class);
                this.fullTextDiff = Arrays.asList(textDiffArray);
            }
            catch (Exception e) {
                //System.out.println(e);
                this.fullTextDiff = null;
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

    public String getFullTextHtml5() {
        return fullTextHtml5;
    }

    public List<BillTextUtils.TextDiff> getFullTextDiff() {
        return fullTextDiff;
    }
}
