package gov.nysenate.openleg.api.legislation.bill.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillId;

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
                    .map(SessionYear::year).orElse(0);
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
