package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.bill.BillSponsor;

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

    protected SponsorView(){
        super();
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
