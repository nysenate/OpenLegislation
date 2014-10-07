package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillInfo;

/**
 * Just the essentials for displaying a Bill in a search result for example.
 */
public class BillInfoView extends BillIdView implements ViewObject
{
    protected String title;
    protected String activeVersion;
    protected String summary;
    protected BillStatusView status;
    protected SponsorView sponsor;

    public BillInfoView(BillInfo billInfo) {
        super(billInfo != null ? billInfo.getBillId() : null);
        if (billInfo != null) {
            title = billInfo.getTitle();
            activeVersion = billInfo.getActiveVersion() != null ? billInfo.getActiveVersion().getValue() : null;
            summary = billInfo.getSummary();
            status = new BillStatusView(billInfo.getStatus());
            sponsor = new SponsorView(billInfo.getSponsor());
        }
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

    public BillStatusView getStatus() {
        return status;
    }

    public SponsorView getSponsor() {
        return sponsor;
    }

    @Override
    public String getViewType() {
        return "bill-info";
    }
}