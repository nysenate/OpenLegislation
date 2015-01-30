package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;

public class BillDiffView implements ViewObject
{
    protected BaseBillIdView billId;
    protected String version1;
    protected String version2;
    protected String diffHtml;

    public BillDiffView(BaseBillIdView billId, String version1, String version2, String diffHtml) {
        this.billId = billId;
        this.version1 = version1;
        this.version2 = version2;
        this.diffHtml = diffHtml;
    }

    public BaseBillIdView getBillId() {
        return billId;
    }

    public String getVersion1() {
        return version1;
    }

    public String getVersion2() {
        return version2;
    }

    public String getDiffHtml() {
        return diffHtml;
    }

    @Override
    public String getViewType() {
        return "bill-diff";
    }
}
