package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.entity.Member;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the bill's sponsor. Typically this would just be a reference to a Member
 * but there are special cases where the bill is sponsored by the RULES committee or
 * it's a budget bill and has no sponsor.
 */
public class BillSponsor implements Serializable
{
    private static final long serialVersionUID = -9140631420743063575L;

    /** Typically a bill will have a single member that sponsored it. */
    private Member member;

    /** Indicates if bill is a budget bill (no member). */
    private boolean budgetBill = false;

    /** Indicates if bill is introduced through RULES. (no member). */
    private boolean rulesSponsor = false;

    /** --- Constructors --- */

    public BillSponsor() {}

    public BillSponsor(Member member) {
        this.member = member;
    }

    /** --- Functional Getters/Setters --- */

    public boolean hasMember() {
        return (member != null);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BillSponsor other = (BillSponsor) obj;
        return Objects.equals(this.member, other.member) &&
               Objects.equals(this.budgetBill, other.budgetBill) &&
               Objects.equals(this.rulesSponsor, other.rulesSponsor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, budgetBill, rulesSponsor);
    }

    @Override
    public String toString() {
        return (((rulesSponsor)
                    ? "RULES " : "")
                + ((budgetBill)
                    ? "BUDGET BILL " : "") +
                ((hasMember())
                    ? ((rulesSponsor)
                       ? "(" + member.getLbdcShortName() + ")"
                       : member.getLbdcShortName())
                    : "")).trim();
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
}
