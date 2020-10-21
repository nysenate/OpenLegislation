package gov.nysenate.openleg.legislation.bill.exception;

import gov.nysenate.openleg.legislation.bill.BillId;

public class BillNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 5754817749566075113L;

    protected BillId billId;

    public BillNotFoundEx(BillId billId) {
        this(billId, null);
    }

    public BillNotFoundEx(BillId billId, Exception ex) {
        super(getMessage(billId), ex);
        this.billId = billId;
    }

    public BillId getBillId() {
        return billId;
    }

    private static String getMessage(BillId billId) {
        return (billId != null)
                ? "Bill " + billId.toString() + " could not be retrieved."
                : "Bill could not be retrieved since the given BillId was null";
    }
}
