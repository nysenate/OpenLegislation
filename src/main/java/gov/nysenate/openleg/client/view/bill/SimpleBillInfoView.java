package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillInfo;

import java.time.LocalDateTime;

/**
 * Even simpler bill info view containing just the id, type, and title of the bill.
 */
public class SimpleBillInfoView extends BaseBillIdView implements ViewObject
{
    protected String printNo;
    protected BillTypeView billType;
    protected String title;
    protected String activeVersion;
    protected int year;
    protected LocalDateTime publishedDateTime;
    protected BaseBillIdView substitutedBy;
    protected SponsorView sponsor;

    public SimpleBillInfoView(BillInfo billInfo) {
        super(billInfo != null ? billInfo.getBillId() : null);
        if (billInfo != null) {
            title = billInfo.getTitle();
            activeVersion = billInfo.getActiveVersion() != null ? billInfo.getActiveVersion().getValue() : null;
            printNo = basePrintNo + (activeVersion!=null ? activeVersion : "");
            year = billInfo.getYear();
            publishedDateTime = billInfo.getPublishedDateTime();
            substitutedBy = billInfo.getSubstitutedBy() != null ? new BaseBillIdView(billInfo.getSubstitutedBy()) : null;
            sponsor = billInfo.getSponsor() != null ? new SponsorView(billInfo.getSponsor()) : null;
            billType = billInfo.getBillId() != null && billInfo.getBillId().getBillType() != null
                    ? new BillTypeView(billInfo.getBillId().getBillType()) : null;
        }
    }

    public String getPrintNo() {
        return printNo;
    }

    public BillTypeView getBillType() {
        return billType;
    }

    public String getTitle() {
        return title;
    }

    public String getActiveVersion() {
        return activeVersion;
    }

    public int getYear() {
        return year;
    }

    public LocalDateTime getPublishedDateTime() {
        return publishedDateTime;
    }

    public BaseBillIdView getSubstitutedBy() {
        return substitutedBy;
    }

    public SponsorView getSponsor() {
        return sponsor;
    }

    @Override
    public String getViewType() {
        return "simple-bill-info";
    }
}
