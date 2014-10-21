package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.model.bill.BillInfo;

/**
 * Just the essentials for displaying a Bill in a search result for example.
 */
public class BillInfoView extends BaseBillIdView implements ViewObject
{
    protected String printNo;
    protected String billType;
    protected String title;
    protected String activeVersion;
    protected String summary;
    protected BillStatusView status;
    protected SponsorView sponsor;
    protected CommitteeIdView committee;

    public BillInfoView(BillInfo billInfo) {
        super(billInfo != null ? billInfo.getBillId() : null);
        if (billInfo != null) {
            title = billInfo.getTitle();
            billType = billInfo.getBillId().getBillType().getName();
            activeVersion = billInfo.getActiveVersion() != null ? billInfo.getActiveVersion().getValue() : null;
            printNo = basePrintNo + activeVersion;
            summary = billInfo.getSummary();
            status = new BillStatusView(billInfo.getStatus());
            sponsor = new SponsorView(billInfo.getSponsor());
            committee = (billInfo.getCurrentCommittee() != null)
                    ? new CommitteeIdView(billInfo.getCurrentCommittee()) : null;
        }
    }

    public String getPrintNo() {
        return printNo;
    }

    public String getTitle() {
        return title;
    }

    public String getBillType() {
        return billType;
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

    public CommitteeIdView getCommittee() {
        return committee;
    }

    @Override
    public String getViewType() {
        return "bill-info";
    }
}