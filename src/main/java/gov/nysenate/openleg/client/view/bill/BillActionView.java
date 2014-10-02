package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillAction;

public class BillActionView implements ViewObject {

    protected BillIdView billId;
    protected String date;
    protected String chamber;
    protected int sequenceNo;
    protected String text;

    public BillActionView(BillAction billAction) {
        if (billAction != null) {
            billId = new BillIdView(billAction.getBillId());
            date = billAction.getDate().toString();
            chamber = billAction.getChamber().name();
            sequenceNo = billAction.getSequenceNo();
            text = billAction.getText();
        }
    }

    public BillIdView getBillId() {
        return billId;
    }

    public String getDate() {
        return date;
    }

    public String getChamber() {
        return chamber;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "bill-action";
    }
}
