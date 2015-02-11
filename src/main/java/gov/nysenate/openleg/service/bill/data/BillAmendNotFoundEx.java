package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.model.bill.BillId;

public class BillAmendNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = 4921161551020029429L;

    protected BillId billId;

    public BillAmendNotFoundEx(BillId billId) {
        super(
            (billId != null) ? "Bill Amendment " + billId.toString() + " was not found."
                             : "Bill Amendment could not be retrieved since the given BillId was null"
        );
        this.billId = billId;
    }

    public BillId getBillId() {
        return billId;
    }
}
