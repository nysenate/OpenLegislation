package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;

import java.io.Serializable;

public class ApprovalMessage extends BaseLegislativeContent implements Serializable, Comparable<ApprovalMessage> {

    private static final long serialVersionUID = -857517100026638357L;

    /** The id of the approved bill */
    BillId billId;

    /** The approval id, good for a single year */
    private int approvalNumber;

    /** The full text of the memo */
    private String memoText;

    /** Todo figure out what this is.  Possibly law chapter */
    private int chapter;

    /** The name of the governor who signed the approval */
    private String signer;

    /** --- Constructors --- */

    public ApprovalMessage(){
        billId = null;
        memoText = null;
        signer = null;
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(ApprovalMessage o) {
        return ComparisonChain.start()
                .compare(this.getApprovalId(),o.getApprovalId())
                .result();
    }

    /** --- Functional getters/setters --- */

    public ApprovalId getApprovalId(){
        return new ApprovalId(this.year, this.approvalNumber);
    }

    /** --- Getters/Setters --- */

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public int getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(int approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getMemoText() {
        return memoText;
    }

    public void setMemoText(String memoText) {
        this.memoText = memoText;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }
}
