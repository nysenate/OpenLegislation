package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BaseBillId;

public class BillFullTextView implements ViewObject
{
    protected BaseBillId baseBillId;
    protected String version;
    protected String fullText;

    public BillFullTextView(BaseBillId baseBillId, String version, String fullText) {
        this.baseBillId = baseBillId;
        this.version = version;
        this.fullText = fullText;
    }

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public String getVersion() {
        return version;
    }

    public String getFullText() {
        return fullText;
    }

    @Override
    public String getViewType() {
        return "bill-fulltext-view";
    }
}
