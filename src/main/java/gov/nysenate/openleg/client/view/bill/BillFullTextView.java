package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillTextFormat;

public class BillFullTextView implements ViewObject
{
    protected BaseBillId baseBillId;
    protected String version;
    protected BillTextFormat fullTextFormat;
    protected String fullText;

    public BillFullTextView(BaseBillId baseBillId, String version, String fullText, BillTextFormat fullTextFormat) {
        this.baseBillId = baseBillId;
        this.version = version;
        this.fullText = fullText;
        this.fullTextFormat = fullTextFormat;
    }

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public String getVersion() {
        return version;
    }

    public BillTextFormat getFullTextFormat() {
        return fullTextFormat;
    }

    public String getFullText() {
        return fullText;
    }

    @Override
    public String getViewType() {
        return "bill-fulltext-view";
    }
}
