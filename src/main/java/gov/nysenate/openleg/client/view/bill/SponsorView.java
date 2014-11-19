package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.bill.BillSponsor;

public class SponsorView implements ViewObject
{
    protected MemberView member;
    protected boolean budget;
    protected boolean rules;

    public SponsorView(BillSponsor billSponsor) {
        if (billSponsor != null) {
            this.member = billSponsor.getMember()!=null ? new MemberView(billSponsor.getMember()) : null;
            this.budget = billSponsor.isBudget();
            this.rules = billSponsor.isRules();
        }
    }

    @Override
    public String getViewType() {
        return "sponsor";
    }

    public MemberView getMember() {
        return member;
    }

    public boolean isBudget() {
        return budget;
    }

    public boolean isRules() {
        return rules;
    }
}
