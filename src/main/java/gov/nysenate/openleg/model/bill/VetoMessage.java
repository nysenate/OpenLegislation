package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;

import java.io.Serializable;
import java.time.LocalDate;

public class VetoMessage extends BaseLegislativeContent implements Serializable, Comparable<VetoMessage>
{
    private static final long serialVersionUID = 8761376058922061047L;

    /** The id of the vetoed bill */
    private BaseBillId billId;

    /** The veto id, good for a single year */
    private int vetoNumber;

    /** The full text of the memo */
    private String memoText;

    /** True if the veto is a line veto */
    private VetoType type;

    /** The chapter of law that the bill would affect */
    private int chapter;

    /** The page of the bill containing the vetoed section */
    private int billPage;

    /** The beginning of the vetoed bill section */
    private int lineStart;

    /** The end of the vetoed bill section */
    private int lineEnd;

    /** The name of the governor who signed the bill */
    private String signer;

    /** The date that the veto memo was signed */
    private LocalDate signedDate;

    /* --- Constructors --- */

    public VetoMessage(){
        super();
    }

    /* --- Overrides --- */

    @Override
    public int compareTo(VetoMessage o) {
        return ComparisonChain.start()
            .compare(this.year, o.year)
            .compare(this.vetoNumber, o.vetoNumber)
            .result();
    }

    /* --- Functional Getters/Setters --- */

    public VetoId getVetoId(){
        return new VetoId(this.year, this.vetoNumber);
    }

    /* --- Basic Getters/Setters --- */

    public BaseBillId getBillId() {
        return billId;
    }

    public void setBillId(BaseBillId billId) {
        this.billId = billId;
    }

    public int getVetoNumber() {
        return vetoNumber;
    }

    public void setVetoNumber(int vetoNumber) {
        this.vetoNumber = vetoNumber;
    }

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }

    public VetoType getType() {
        return type;
    }

    public void setType(VetoType type) {
        this.type = type;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getBillPage() {
        return billPage;
    }

    public void setBillPage(int billPage) {
        this.billPage = billPage;
    }

    public int getLineStart() {
        return lineStart;
    }

    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(LocalDate signedDate) {
        this.signedDate = signedDate;
    }
}