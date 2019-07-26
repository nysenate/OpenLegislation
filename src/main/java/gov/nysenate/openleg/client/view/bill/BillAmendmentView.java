package gov.nysenate.openleg.client.view.bill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.util.BillTextUtils;

import java.time.LocalDate;
import java.util.*;
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
    protected ListView<MemberView> coSponsors;
    protected ListView<MemberView> multiSponsors;
    protected boolean uniBill;
    protected boolean isStricken;
    protected MapView<String, ListView<String>> relatedLaws;
    protected MapView<String, ListView<String>> relatedLawUrls;
    protected BillAmendmentView(){}

    public BillAmendmentView(Bill bill, BillAmendment billAmendment, PublishStatus publishStatus) {
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
            this.coSponsors = ListView.of(billAmendment.getCoSponsors().stream()
                .map(MemberView::new)
                .collect(Collectors.toList()));
            this.multiSponsors = ListView.of(billAmendment.getMultiSponsors().stream()
                .map(MemberView::new)
                .collect(Collectors.toList()));
            this.uniBill = billAmendment.isUniBill();
            this.isStricken = billAmendment.isStricken();

            Map<String, ListView<String>> relatedLawNames = new HashMap<>();
            billAmendment.getRelatedLawsMap().forEach((k,v) ->
                    relatedLawNames.put(k, ListView.ofStringList(v)));
            this.relatedLaws = MapView.of(relatedLawNames);
            this.relatedLawUrls = getRelatedLawUrls(bill, billAmendment, publishStatus);
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

    public MapView<String, ListView<String>> getRelatedLawUrls() {
        return relatedLawUrls;
    }

    public MapView<String, ListView<String>> getRelatedLaws() {
        return relatedLaws;
    }


    private static MapView<String, ListView<String>> getRelatedLawUrls(Bill bill, BillAmendment amd, PublishStatus publishStatus) {
        // Converts the map of LawActionType->{LawDocId} to a view of LawActionType->{Valid law url}
        Map<String, List<String>> relatedLaws = amd.getRelatedLawsMap();
        Map<String, ListView<String>> view = new HashMap<>();
        BillStatusType status = bill.getStatus().getStatusType();
        boolean passed = (status.equals(BillStatusType.SIGNED_BY_GOV) || status.equals(BillStatusType.ADOPTED));

        for (String lawAction : relatedLaws.keySet()) {
            List<String> urls = new LinkedList<>();
            // The date for most law links will be when the bill was proposed
            String date = publishStatus.getEffectDateTime().toString().substring(0,10);
            // If the bill introduced a new law, then we have to link to the law when the bill was passed
            if (passed && lawAction.equals("ADD")) {
                date = bill.getStatus().getActionDate().toString();
            }
            boolean amdExists = !(lawAction.equals("ADD") && !passed);
            for (String lawDoc : relatedLaws.get(lawAction)) {
                urls.add(getLawUrl(date, lawDoc, amdExists));
            }
            view.put(lawAction, ListView.ofStringList(urls));
        }
        return MapView.of(view);
    }

    private static String getLawUrl(String date, String lawDoc, boolean exists) {
        // Form a URL from the yyyy-dd-mm date, the lawDocId, and an indicator whether the specific law can be linked
        String url =  "/laws/" + lawDoc.substring(0, 3) + "?date=" + date;
        if (lawDoc.toLowerCase().contains("generally") || !exists) {
            return url;
        }
        else {
            return url + "&location=" + lawDoc.substring(3);
        }
    }
}
