package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.entity.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the bill's sponsor. Typically this would just be a reference to a Member
 * but there are special cases where the bill is sponsored by the RULES committee or
 * it's a budget bill and has no sponsor.
 */
public class BillSponsor
{
    /** Typically a bill will have a single member that sponsored it. */
    private Member member;

    /** Indicates if bill is a budget bill (no member). */
    private boolean budgetBill = false;

    /** Indicates if bill is introduced through RULES. (no member). */
    private boolean rulesSponsor = false;

    /** A RULES sponsor can have a list of members that requested it. */
    private List<Member> rulesRequestMembers;

    /** --- Constructors --- */

    public BillSponsor() {}

    public BillSponsor(Member member) {
        this.member = member;
        this.rulesRequestMembers = new ArrayList<>();
    }

    /** --- Functional Getters/Setters --- */

    public boolean hasMember() {
        return (member != null);
    }

    /** --- Basic Getters/Setters --- */

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public boolean isBudgetBill() {
        return budgetBill;
    }

    public void setBudgetBill(boolean budgetBill) {
        this.budgetBill = budgetBill;
    }

    public boolean isRulesSponsor() {
        return rulesSponsor;
    }

    public void setRulesSponsor(boolean rulesSponsor) {
        this.rulesSponsor = rulesSponsor;
    }

    public List<Member> getRulesRequestMembers() {
        return rulesRequestMembers;
    }

    public void setRulesRequestMembers(List<Member> rulesRequestMembers) {
        this.rulesRequestMembers = rulesRequestMembers;
    }
}
