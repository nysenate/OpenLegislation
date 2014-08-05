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

    /**
     * The BaseBillId constructor will set the version to a default value regardless
     * of the supplied print no string.
     */
    public BaseBillId(String printNo, int session) {
        super(printNo, session);
        super.version = BASE_VERSION;
    }

    /**
     * Return a new BillId instance with the version set as the supplied 'version'.
     * This can be useful when moving from a non-version context (such as a Bill container)
     * to a version specific context (BillAmendment).
     */
    public BillId withVersion(String version) {
        return new BillId(this.basePrintNo, this.session, this.version);
    }

    /**
     * Return the base version by default since version info is to be ignored.
     */
    @Override
    public String getVersion() {
        return BASE_VERSION;
    }
}