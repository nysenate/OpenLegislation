package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.entity.SessionMember;

import java.io.Serializable;
import java.util.*;

/**
 * A BillAmendment contains all that data that is unique to amendments of a bill. Data
 * that is common to amendments of the same bill will be exposed through the
 * {@link gov.nysenate.openleg.model.bill.Bill} object.
 */
public class BillAmendment implements Serializable, Cloneable
{
    private static final long serialVersionUID = -2020934234685630361L;

    /** The parent base bill id. */
    protected BaseBillId baseBillId;

    /** Amendment version (e.g DEFAULT, A, B, C, etc). */
    protected Version version = BillId.DEFAULT_VERSION;

    /** The "sameAs" bill in the other chamber that matches this version.
        There can be multiple same as bills in some cases, typically just 0 or 1 though. */
    protected Set<BillId> sameAs = new HashSet<>();

    /** Opinion from a bill's sponsor as to what the bill will accomplish and why it should become
     * a law. Usually it's written in more easily understandable language. */
    protected String memo = "";

    /** The section of the law the bill affects. e.g (Vehicle And Traffic) */
    protected String lawSection = "";

    /** The law code of the bill. e.g (Amd §1373, Pub Health L) */
    protected String law = "";

    /** The AN ACT TO... clause which describes the bill's intent. */
    protected String actClause = "";

    /** The full text of the amendment. */
    protected String fullText = "";

    /** The committee the bill is currently referred to, if any. */
    protected CommitteeVersionId currentCommittee = null;

    /** List of co-sponsors for the amendment. It's a list of Legislators who share credit for
     *  introducing a bill. */
    protected List<SessionMember> coSponsors = new ArrayList<>();

    /** List of multi-sponsors for the amendment. */
    protected List<SessionMember> multiSponsors = new ArrayList<>();

    /** A flag marking this bill as stricken (effectively withdrawn) */
    protected Boolean stricken = false;

    /** A list of votes that have been made on this bill. Maps the vote id -> BillVote to make it
     *  easy to update votes. */
    protected Map<BillVoteId, BillVote> votesMap = new TreeMap<>();

    /** A flag marking this bill as introduced in unison in both houses */
    protected Boolean uniBill = false;

    /** --- Constructors --- */

    public BillAmendment(BaseBillId baseBillId, Version version) {
        if (version == null) {
            throw new IllegalArgumentException("Cannot create BillAmendment with null version");
        }
        this.baseBillId = baseBillId;
        this.version = version;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return this.getBillId().toString();
    }

    /**
     * Creates a shallow clone for this amendment. This should only be used for caching purposes.
     * @return BillAmendment
     */
    public BillAmendment shallowClone() {
        try {
            return (BillAmendment) this.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone bill amendment!");
        }
    }

    /** --- Functional Getters/Setters --- */

    public String getBasePrintNo() {
        return baseBillId.getBasePrintNo();
    }

    public SessionYear getSession() {
        return baseBillId.getSession();
    }

    public BillId getBillId() {
        return baseBillId.withVersion(this.version);
    }

    public boolean isSenateBill() {
        return this.getBillId().getBillType().getChamber().equals(Chamber.SENATE);
    }

    public boolean isAssemblyBill() {
        return this.getBillId().getBillType().getChamber().equals(Chamber.ASSEMBLY);
    }

    public BillType getBillType() {
        return this.getBillId().getBillType();
    }

    public void updateVote(BillVote vote) {
        this.votesMap.put(vote.getVoteId(), vote);
    }

    public boolean isBaseVersion() {
        return BillId.isBaseVersion(this.version);
    }

    public boolean isResolution() {
        return this.getBillType().isResolution();
    }

    /** --- Basic Getters/Setters --- */

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public Version getVersion() {
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

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public CommitteeVersionId getCurrentCommittee() {
        return currentCommittee;
    }

    public void setCurrentCommittee(CommitteeVersionId currentCommittee) {
        this.currentCommittee = currentCommittee;
    }

    public List<SessionMember> getCoSponsors() {
        return coSponsors;
    }

    public void setCoSponsors(List<SessionMember> coSponsors) {
        this.coSponsors = coSponsors;
    }

    public List<SessionMember> getMultiSponsors() {
        return multiSponsors;
    }

    public void setMultiSponsors(List<SessionMember> multiSponsors) {
        this.multiSponsors = multiSponsors;
    }

    public Boolean isStricken() {
        return stricken;
    }

    public void setStricken(Boolean stricken) {
        this.stricken = stricken;
    }

    public Map<BillVoteId, BillVote> getVotesMap() {
        return votesMap;
    }

    public List<BillVote> getVotesList() {
        return new ArrayList<>(votesMap.values());
    }

    public void setVotesMap(List<BillVote> votesMap) {
        for (BillVote vote : votesMap) {
            this.votesMap.put(vote.getVoteId(), vote);
        }
    }

    public Boolean isUniBill() {
        return uniBill;
    }

    public void setUniBill(Boolean uniBill) {
        this.uniBill = uniBill;
    }

    public String getLawSection() {
        return lawSection;
    }

    public void setLawSection(String lawSection) {
        this.lawSection = lawSection;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }
}