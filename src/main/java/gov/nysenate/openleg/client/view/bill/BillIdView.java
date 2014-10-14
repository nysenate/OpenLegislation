package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillId;

public class BillIdView implements ViewObject
{
    protected String printNo;
    protected String basePrintNo;
    protected String version;
    protected int session;

    public BillIdView(BillId billId) {
        if (billId != null) {
            this.printNo = billId.getPrintNo();
            this.basePrintNo = billId.getBasePrintNo();
            this.version = billId.getVersion().getValue();
            this.session = billId.getSession().getYear();
        }
    }

    public String getPrintNo() {
        return printNo;
    }

    public String getBasePrintNo() {
        return basePrintNo;
    }

    public String getVersion() {
        return version;
    }

    public int getSession() {
        return session;
    }

    @Override
    public String getViewType() {
        return "bill-id";
    }
}
