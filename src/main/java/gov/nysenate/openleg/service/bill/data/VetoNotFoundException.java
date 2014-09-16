package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.VetoId;

public class VetoNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -2752980385510096064L;

    protected VetoId vetoId;
    protected BaseBillId baseBillId;

    public VetoNotFoundException(Throwable cause, VetoId vetoId) {
        super(
                "VetoMessage "+ vetoId + " could not be retrieved"
                , cause);
        this.vetoId = vetoId;
    }

    public VetoNotFoundException(Throwable cause, BaseBillId baseBillId) {
        super(
                "Could not retrieve vetos for " + baseBillId
                , cause);
        this.baseBillId = baseBillId;
    }

    public VetoId getVetoId() {
        return vetoId;
    }

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }
}
