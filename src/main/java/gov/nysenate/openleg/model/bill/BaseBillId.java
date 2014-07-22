package gov.nysenate.openleg.model.bill;

public class BaseBillId extends BillId{

    public BaseBillId(String printNo, int session) {
        super(printNo, session);
    }

    @Override
    public String getVersion() {
        return BASE_VERSION;
    }
}
