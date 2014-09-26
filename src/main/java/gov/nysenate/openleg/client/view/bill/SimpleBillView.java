package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.Bill;

import java.util.ArrayList;
import java.util.List;

/**
 * Just the essentials for displaying a Bill in a search result for example.
 */
public class SimpleBillView implements ViewObject
{
    protected BillIdView billId;
    protected String title;
    protected String activeVersion;
    protected String summary;
    protected List<String> amendmentVersions;
    protected String lawSection;
    protected String lawCode;

    public SimpleBillView(Bill bill) {
        if (bill != null) {
            billId = new BillIdView(bill.getBaseBillId());
            title = bill.getTitle();
            activeVersion = bill.getActiveVersion().getValue();
            summary = bill.getSummary();
            amendmentVersions = new ArrayList<>();
            // Include only the amendment versions that are published
            bill.getAmendPublishStatusMap().forEach((k,v) -> {
                if (v.isPublished()) amendmentVersions.add(k.getValue());
            });
            lawSection = bill.getLawSection();
            lawCode = bill.getLaw();
        }
    }

    public BillIdView getBillId() {
        return billId;
    }

    public String getTitle() {
        return title;
    }

    public String getActiveVersion() {
        return activeVersion;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getAmendmentVersions() {
        return amendmentVersions;
    }

    public String getLawSection() {
        return lawSection;
    }

    public String getLawCode() {
        return lawCode;
    }
}