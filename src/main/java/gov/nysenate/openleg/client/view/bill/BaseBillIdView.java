package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillId;

public class BaseBillIdView implements ViewObject
{
    protected String basePrintNo;
    protected int session;
    protected String basePrintNoStr;

    public BaseBillIdView(BillId billId) {
        if (billId != null) {
            this.basePrintNo = billId.getBasePrintNo();
            this.session = billId.getSession().getYear();
            this.basePrintNoStr = billId.toString();
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

    public String getBasePrintNoStr() {
        return basePrintNoStr;
    }
}
