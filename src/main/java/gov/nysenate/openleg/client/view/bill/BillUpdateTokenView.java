package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillUpdateToken;

import java.time.LocalDateTime;

public class BillUpdateTokenView implements ViewObject
{
    private BaseBillIdView billId;
    private LocalDateTime lastUpdatedOn;

    public BillUpdateTokenView(BillUpdateToken token) {
        if (token != null) {
            this.billId = new BaseBillIdView(token.getBillId());
            this.lastUpdatedOn = token.getUpdatedDateTime();
        }
    }

    @Override
    public String getViewType() {
        return "bill-update-token";
    }

    public BaseBillIdView getBillId() {
        return billId;
    }

    public LocalDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }
}