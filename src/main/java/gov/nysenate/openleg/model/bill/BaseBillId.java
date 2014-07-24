package gov.nysenate.openleg.model.bill;

import java.io.Serializable;

public class BaseBillId extends BillId implements Serializable
{
    private static final long serialVersionUID = -7708296547127325102L;

    public BaseBillId(String printNo, int session) {
        super(printNo, session);
    }

    @Override
    public String getVersion() {
        return BASE_VERSION;
    }
}