package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillId;

public class BaseBillIdView implements ViewObject
{
    protected String basePrintNo;
    protected int session;

    public BaseBillIdView(BillId billId) {
        if (billId != null) {
            this.basePrintNo = billId.getBasePrintNo();
            this.session = billId.getSession().getYear();
        }
    }

    @Override
    public String getViewType() {
        return "base-bill-id";
    }

    public String getBasePrintNo() {
        return basePrintNo;
    }

    public int getSession() {
        return session;
    }
}
