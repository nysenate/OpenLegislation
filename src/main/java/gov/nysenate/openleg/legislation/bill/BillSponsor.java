package gov.nysenate.openleg.legislation.bill;

import gov.nysenate.openleg.legislation.member.SessionMember;

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

    /** Indicates if bill is introduced by the Independent Redistricting Commission. */
    private boolean redistricting = false;

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
               Objects.equals(this.rules, other.rules) &&
                Objects.equals(this.redistricting, other.redistricting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, budget, rules, redistricting);
    }

    @Override
    public String toString() {
        if (!rules & !budget & !redistricting) {
            return member.getLbdcShortName();
        }

        String s = "";
        if (rules) {
            s = "RULES";
        }
        else if (budget) {
            s = "BUDGET BILL";
        }
        else if (redistricting) {
            s = "REDISTRICTING";
        }

        if (hasMember()) {
            s += " (" + member.getLbdcShortName() + ")";
        }
        return s.trim();
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

    public boolean isRedistricting() {
        return redistricting;
    }

    public void setRedistricting(boolean redistricting) {
        this.redistricting = redistricting;
    }
}
