package gov.nysenate.openleg.client.view.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.entity.Chamber;

import java.time.LocalDate;

public class BillActionView implements ViewObject {

    protected BillIdView billId;
    protected String date;
    protected String chamber;
    protected int sequenceNo;
    protected String text;

    protected BillActionView() {}

    public BillActionView(BillAction billAction) {
        if (billAction != null) {
            billId = new BillIdView(billAction.getBillId());
            date = billAction.getDate().toString();
            chamber = billAction.getChamber().name();
            sequenceNo = billAction.getSequenceNo();
            text = billAction.getText();
        }
    }

    @JsonIgnore
    public BillAction toBillAction() {
        return new BillAction(
                LocalDate.parse(date),
                text,
                Chamber.getValue(chamber),
                sequenceNo,
                billId.toBillId()
        );
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
