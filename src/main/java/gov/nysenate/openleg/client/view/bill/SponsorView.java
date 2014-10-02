package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillSponsor;

public class SponsorView implements ViewObject
{
    protected String sponsor;

    public SponsorView(BillSponsor billSponsor) {
        if (billSponsor != null) {
            this.sponsor = billSponsor.toString();
        }
    }

    @Override
    public String getViewType() {
        return "sponsor";
    }

    public String getSponsor() {
        return sponsor;
    }
}
