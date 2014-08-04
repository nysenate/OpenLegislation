package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.bill.BillId;

public class BillNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 5754817749566075113L;

    protected BillId billId;

    public BillNotFoundEx(BillId billId, Exception ex) {
        super(
            (billId != null) ? "Bill " + billId.toString() + " could not be retrieved."
                             : "Bill could not be retrieved since the given BillId was null",
            ex
        );
    }

    public BillId getBillId() {
        return billId;
    }
}
