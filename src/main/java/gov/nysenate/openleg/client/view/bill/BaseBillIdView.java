package gov.nysenate.openleg.client.view.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;

import java.util.Optional;

public class BaseBillIdView implements ViewObject
{
    protected String basePrintNo;
    protected int session;
    protected String basePrintNoStr;

    protected BaseBillIdView() {}

    public BaseBillIdView(BillId billId) {
        if (billId != null) {
            this.basePrintNo = billId.getBasePrintNo();
            this.session = Optional.ofNullable(billId.getSession())
                    .map(SessionYear::getYear).orElse(null);
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
