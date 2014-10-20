package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.model.bill.BillSponsor;

public class SponsorView implements ViewObject
{
    protected MemberView member;

    protected boolean budgetBill;

    protected boolean rulesSponsor;

    public SponsorView(BillSponsor billSponsor) {
        if (billSponsor != null) {
            this.member = billSponsor.getMember()!=null ? new MemberView(billSponsor.getMember()) : null;
            this.budgetBill = billSponsor.isBudgetBill();
            this.rulesSponsor = billSponsor.isRulesSponsor();
        }
    }

    @Override
    public String getViewType() {
        return "sponsor";
    }

    public MemberView getMember() {
        return member;
    }

    public boolean isBudgetBill() {
        return budgetBill;
    }

    public boolean isRulesSponsor() {
        return rulesSponsor;
    }
}
