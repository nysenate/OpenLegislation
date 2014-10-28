package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillId;

public class BillIdView extends BaseBillIdView implements ViewObject
{
    protected String printNo;
    protected String version;

    public BillIdView(BillId billId) {
        super(billId);
        if (billId != null) {
            this.printNo = billId.getPrintNo();
            this.version = billId.getVersion().getValue();
        }
    }

    public String getPrintNo() {
        return printNo;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getViewType() {
        return "bill-id";
    }
}
