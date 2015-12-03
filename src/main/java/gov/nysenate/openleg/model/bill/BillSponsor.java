package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.entity.SessionMember;

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
    private SessionMember member;

    /** Indicates if bill is a budget bill (no member). */
    private boolean budget = false;

    /** Indicates if bill is introduced through RULES. (no member). */
    private boolean rules = false;

    /** --- Constructors --- */

    public BillSponsor() {}

    public BillSponsor(SessionMember member) {
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
               Objects.equals(this.budget, other.budget) &&
               Objects.equals(this.rules, other.rules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, budget, rules);
    }

    @Override
    public String toString() {
        return (((rules)
                    ? "RULES " : "")
                + ((budget)
                    ? "BUDGET BILL " : "") +
                ((hasMember())
                    ? ((rules)
                       ? "(" + member.getLbdcShortName() + ")"
                       : member.getLbdcShortName())
                    : "")
        ).trim();
    }

    /** --- Basic Getters/Setters --- */

    public SessionMember getMember() {
        return member;
    }

    public void setMember(SessionMember member) {
        this.member = member;
    }

    public boolean isBudget() {
        return budget;
    }

    public void setBudget(boolean budget) {
        this.budget = budget;
    }

    public boolean isRules() {
        return rules;
    }

    public void setRules(boolean rules) {
        this.rules = rules;
    }
}
