package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.entity.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillAmendment extends BaseObject
{
    /** Print number of the base bill. */
    protected String baseBillPrintNo = "";

    /** Amendment version, (e.g "A"). */
    protected String version = "";

    /** The "sameAs" bill in the other chamber that matches this version.
     *  There can be multiple same as bills in some cases, typically just 0 or 1 though. */
    protected Set<BillId> sameAs = new HashSet<>();

    /** The sponsor memo of the amendment. */
    protected String memo = "";

    /** The AN ACT TO... clause for the bill version. */
    protected String actClause = "";

    /** The full text of the amendment. */
    protected String fulltext;

    /** The committee the bill is currently referred to, if any. */
    protected String currentCommittee = "";

    /** List of co-sponsors for the amendment. */
    protected List<Person> coSponsors = new ArrayList<>();

    /** List of multi-sponsors for the amendment. */
    protected List<Person> multiSponsors = new ArrayList<>();

    /** A flag marking this bill as stricken (effectively withdrawn) */
    protected Boolean stricken = false;

    /** A list of votes that have been made on this bill. */
    protected List<Vote> votes = new ArrayList<>();

    /** A flag marking this bill as introduced in unison in both houses **/
    protected Boolean uniBill = false;

    public BillAmendment() {
        super();
    }

    public BillAmendment(Bill baseBill, String version) {
        this();
        this.baseBillPrintNo = baseBill.getPrintNo();
        this.session = baseBill.getSession();
        this.year = baseBill.getYear();
        this.version = version;
    }

    /** --- Functional Getters/Setters --- */

    public String getBillId() {
        return this.baseBillPrintNo + this.version + "-" + this.session;
    }

    public void setVersion(String version) {
        this.version = version.trim().toUpperCase();
    }

    public void updateVote(Vote vote) {
        this.getVotes().remove(vote);
        this.getVotes().add(vote);
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

    public List<Person> getCoSponsors() {
        return coSponsors;
    }

    public void setCoSponsors(List<Person> coSponsors) {
        this.coSponsors = coSponsors;
    }

    public List<Person> getMultiSponsors() {
        return multiSponsors;
    }

    public void setMultiSponsors(List<Person> multiSponsors) {
        this.multiSponsors = multiSponsors;
    }

    public Boolean isStricken() {
        return stricken;
    }

    public void setStricken(Boolean stricken) {
        this.stricken = stricken;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public Boolean isUniBill() {
        return uniBill;
    }

    public void setUniBill(Boolean uniBill) {
        this.uniBill = uniBill;
    }
}