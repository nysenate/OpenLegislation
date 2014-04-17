package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillAmendment extends BaseObject
{
    protected Set<BillSection> sectionsSet = new HashSet<>();

    /** Id of the base bill. */
    protected String baseBillId = "";

    /** Print number of the base bill. */
    protected String baseBillPrintNo = "";

    /** Amendment version, (e.g "A"). */
    protected String version = "";

    /** The "sameAs" bill in the other chamber that matches this version. */
    protected String sameAs = "";

    /** The sponsor memo of the amendment. */
    protected String memo = "";

    /** The law code of the amendment. */
    protected String law = "";

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

    public BillAmendment(Bill baseBill, String version)
    {
        this();
        this.baseBillPrintNo = baseBill.getPrintNumber();
        this.baseBillId = baseBill.getBillId();
        this.session = baseBill.getSession();
        this.year = baseBill.getYear();
        this.version = version;
    }

    public String getBaseBillId()
    {
        return baseBillId;
    }

    public void setBaseBillId(String baseBillId)
    {
        this.baseBillId = baseBillId;
    }

    public String getBaseBillPrintNo() {
        return baseBillPrintNo;
    }

    public void setBaseBillPrintNo(String baseBillPrintNo) {
        this.baseBillPrintNo = baseBillPrintNo;
    }

    public String getBillId()
    {
        return this.baseBillPrintNo + this.version + "-" + this.session;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version.trim().toUpperCase();
    }

    public Set<BillSection> getSectionsSet()
    {
        return sectionsSet;
    }

    public String getSameAs()
    {
        return sameAs;
    }

    public void setSameAs(String sameAs)
    {
        this.sameAs = sameAs;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public String getLaw()
    {
        return law;
    }

    public void setLaw(String law)
    {
        this.law = law;
    }

    public String getActClause()
    {
        return actClause;
    }

    public void setActClause(String actClause)
    {
        this.actClause = actClause;
    }

    public String getFulltext()
    {
        return fulltext;
    }

    public void setFulltext(String fulltext)
    {
        this.fulltext = fulltext;
    }

    public String getCurrentCommittee()
    {
        return currentCommittee;
    }

    public void setCurrentCommittee(String currentCommittee)
    {
        this.currentCommittee = currentCommittee;
    }

    public List<Person> getCoSponsors()
    {
        return coSponsors;
    }

    public void setCoSponsors(List<Person> coSponsors)
    {
        this.coSponsors = coSponsors;
    }

    public List<Person> getMultiSponsors()
    {
        return multiSponsors;
    }

    public void setMultiSponsors(List<Person> multiSponsors)
    {
        this.multiSponsors = multiSponsors;
    }

    public Boolean isStricken()
    {
        return stricken;
    }

    public void setStricken(Boolean stricken)
    {
        this.stricken = stricken;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @Override
    public String getOid()
    {
        return this.getBillId();
    }

    @Override
    public String getOtype()
    {
        return "billamendment";
    }

    public Boolean isUniBill()
    {
        return uniBill;
    }

    public void setUniBill(Boolean uniBill)
    {
        this.uniBill = uniBill;
    }
}
