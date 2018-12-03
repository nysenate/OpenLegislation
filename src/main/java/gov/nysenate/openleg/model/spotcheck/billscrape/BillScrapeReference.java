package gov.nysenate.openleg.model.spotcheck.billscrape;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kyle on 3/3/15.
 */
public class BillScrapeReference {

    //print number for referenced bill eg. "S100"
    private BaseBillId baseBillId;
    //DateTime this reference was generated
    private LocalDateTime referenceDate;
    //main text of the bill
    private String text;
    //text in the memo of the bill
    private String memo;
    private Set<BillScrapeVote> votes;
    private Version activeVersion;

    /** If a scraped reference could not be found/parsed this is set to true */
    private boolean notFound = false;

    public BillScrapeReference(){}

    /**
     *  @param billId
     * @param referenceDate
     * @param text
     * @param memo
     * @param notFound
     */
    private BillScrapeReference(BillId billId, LocalDateTime referenceDate, String text, String memo,
                                boolean notFound){
        this.baseBillId = BillId.getBaseId(billId);
        this.activeVersion = billId.getVersion();
        this.votes = new HashSet<>();

        this.referenceDate = referenceDate;
        this.text = text;
        this.memo = memo;
        this.notFound = notFound;
    }

    public BillScrapeReference(BillId billId, LocalDateTime referenceDate, String text, String memo) {
        this(billId, referenceDate, text, memo, false);
    }

    public static BillScrapeReference getErrorBtr(BillId billId, LocalDateTime referenceDate,
                                                  String text) {
        return new BillScrapeReference(billId, referenceDate, text, "", true);
    }

    /* --- Functional Getters --- */

    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.LBDC_SCRAPED_BILL, this.referenceDate);
    }

    public BillId getBillId() {
        return new BillId(baseBillId, activeVersion);
    }

    public String getPrintNo() {
        return baseBillId.getPrintNo();
    }

    /* --- Getters / Setters --- */

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public void setBaseBillId(BaseBillId baseBillId) {
        this.baseBillId = baseBillId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getReferenceDate() {
        return referenceDate;
    }
    public void setReferenceDate(LocalDateTime referenceDate) {
        this.referenceDate = referenceDate;
    }

    public int getSessionYear() {
        return baseBillId.getSession().getYear();
    }

    public String getMemo(){
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }
    public Version getActiveVersion(){
        return activeVersion;
    }
    public void setActiveVersion(Version activeVersion) {
        this.activeVersion = activeVersion;
    }

    public Set<BillScrapeVote> getVotes() {
        return votes;
    }

    public void setVotes(Set<BillScrapeVote> votes) {
        this.votes = votes;
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }
}
