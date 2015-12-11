package gov.nysenate.openleg.client.view.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;

public class BaseBillIdView implements ViewObject
{
    protected String basePrintNo;
    protected int session;
    protected String basePrintNoStr;

    protected BaseBillIdView() {}

    public BaseBillIdView(BillId billId) {
        if (billId != null) {
            this.basePrintNo = billId.getBasePrintNo();
            this.session = billId.getSession().getYear();
            this.basePrintNoStr = BaseBillId.of(billId).toString();
        }
    }

    @JsonIgnore
    public BaseBillId toBaseBillId() {
        return new BaseBillId(basePrintNo, session);
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
