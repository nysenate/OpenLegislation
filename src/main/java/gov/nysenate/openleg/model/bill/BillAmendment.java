package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseLegislativeContent;
import gov.nysenate.openleg.model.entity.Member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillAmendment extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = -2020934234685630361L;

    /** Print number of the base bill. */
    protected String baseBillPrintNo = "";

    /** Amendment version, (e.g "A"). */
    protected String version = "";

    /** The "sameAs" bill in the other chamber that matches this version.
        There can be multiple same as bills in some cases, typically just 0 or 1 though. */
    protected Set<BillId> sameAs = new HashSet<>();

    /** Opinion from a bill's sponsor as to what the bill will accomplish and why it should become
     * a law. Usually it's written in more easily understandable language. */
    protected String memo = "";

    /** The AN ACT TO... clause which describes the bill's intent. */
    protected String actClause = "";

    /** The full text of the amendment. */
    protected String fulltext = "";

    /** The committee the bill is currently referred to, if any. */
    protected String currentCommittee = "";

    /** List of co-sponsors for the amendment. It's a list of Legislators who share credit for
     *  introducing a bill. */
    protected List<Member> coSponsors = new ArrayList<>();

    /** List of multi-sponsors for the amendment. */
    protected List<Member> multiSponsors = new ArrayList<>();

    /** A flag marking this bill as stricken (effectively withdrawn) */
    protected Boolean stricken = false;

    /** A list of votes that have been made on this bill. */
    protected List<BillVote> votes = new ArrayList<>();

    /** A flag marking this bill as introduced in unison in both houses */
    protected Boolean uniBill = false;

    /** --- Constructors --- */

    public BillAmendment() {
        super();
    }

    public BillAmendment(BillId baseBillId, String version) {
        this();
        this.baseBillPrintNo = baseBillId.getPrintNo();
        this.session = year = baseBillId.getSession();
        this.version = version;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return this.getBillId().toString();
    }

    /** --- Functional Getters/Setters --- */

    public BillId getBillId() {
        return new BillId(this.baseBillPrintNo, this.session, this.version);
    }

    public BillType getBillType() {
        return this.getBillId().getBillType();
    }

    public void setVersion(String version) {
        this.version = version.trim().toUpperCase();
    }

    public void updateVote(BillVote vote) {
        this.getVotes().remove(vote);
        this.getVotes().add(vote);
    }

    public boolean isBaseVersion() {
        return BillId.isBaseVersion(this.version);
    }

    /** --- Basic Getters/Setters --- */

    public String getBaseBillPrintNo() {
        return baseBillPrintNo;
    }

    public void setBaseBillPrintNo(String baseBillPrintNo) {
        this.baseBillPrintNo = baseBillPrintNo;
    }

    public String getVersion() {
        return version;
    }

    public Set<BillId> getSameAs() {
        return sameAs;
    }

    public void setSameAs(Set<BillId> sameAs) {
        this.sameAs = sameAs;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getActClause() {
        return actClause;
    }

    public void setActClause(String actClause) {
        this.actClause = actClause;
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public String getCurrentCommittee() {
        return currentCommittee;
    }

    public void setCurrentCommittee(String currentCommittee) {
        this.currentCommittee = currentCommittee;
    }

    public List<Member> getCoSponsors() {
        return coSponsors;
    }

    public void setCoSponsors(List<Member> coSponsors) {
        this.coSponsors = coSponsors;
    }

    public List<Member> getMultiSponsors() {
        return multiSponsors;
    }

    public void setMultiSponsors(List<Member> multiSponsors) {
        this.multiSponsors = multiSponsors;
    }

    public Boolean isStricken() {
        return stricken;
    }

    public void setStricken(Boolean stricken) {
        this.stricken = stricken;
    }

    public List<BillVote> getVotes() {
        return votes;
    }

    public void setVotes(List<BillVote> votes) {
        this.votes = votes;
    }

    public Boolean isUniBill() {
        return uniBill;
    }

    public void setUniBill(Boolean uniBill) {
        this.uniBill = uniBill;
    }
}