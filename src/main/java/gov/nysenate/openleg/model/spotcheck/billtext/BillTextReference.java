package gov.nysenate.openleg.model.spotcheck.billtext;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.util.BillTextUtils;

import java.time.LocalDateTime;

/**
 * Created by kyle on 3/3/15.
 */
public class BillTextReference {

    //print number for referenced bill eg. "S100"
    private BaseBillId baseBillId;
    //DateTime this reference was generated
    private LocalDateTime referenceDate;
    //main text of the bill
    private String text;
    //text in the memo of the bill
    private String memo;

    private Version activeVersion;

    /** If a scraped reference could not be found/parsed this is set to true */
    private boolean notFound = false;

    public BillTextReference(){}

    /**
     *  @param billId
     * @param referenceDate
     * @param text
     * @param memo
     * @param notFound
     */
    private BillTextReference(BillId billId, LocalDateTime referenceDate, String text, String memo,
                             boolean notFound){
        this.baseBillId = BillId.getBaseId(billId);
        this.activeVersion = billId.getVersion();

        this.referenceDate = referenceDate;
        this.text = text;
        this.memo = memo;
        this.notFound = notFound;
    }

    public BillTextReference(BillId billId, LocalDateTime referenceDate, String text, String memo) {
        this(billId, referenceDate, text, memo, false);
    }

    public static BillTextReference getErrorBtr(BillId billId, LocalDateTime referenceDate,
                                                String text) {
        return new BillTextReference(billId, referenceDate, text, "", true);
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

    public String getStripedText() {
        return BillTextUtils.parseHTMLtext(text);
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

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }
}
