package gov.nysenate.openleg.model.bill;

import java.io.Serializable;

/**
 * The BaseBillId is a subclass of BillId that ensures that the version is always
 * set as the base version. This class can be useful when you want to reference
 * Bill containers where the amendment version is irrelevant.
 */
public class BaseBillId extends BillId implements Serializable
{
    private static final long serialVersionUID = -7708296547127325102L;

    public BaseBillId(String printNo, int session) {
        super(printNo, session);
        super.version = BASE_VERSION;
    }

    @Override
    public String getVersion() {
        return BASE_VERSION;
    }
}