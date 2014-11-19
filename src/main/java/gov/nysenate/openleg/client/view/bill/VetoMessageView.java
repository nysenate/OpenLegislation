package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.VetoMessage;

import java.time.LocalDate;

public class VetoMessageView implements ViewObject
{
    protected BillIdView billId;
    protected int year;
    protected int vetoNumber;
    protected String memoText;
    protected String vetoType;
    protected int chapter;
    protected int billPage;
    protected int lineStart;
    protected int lineEnd;
    protected String signer;
    protected LocalDate signedDate;

    public VetoMessageView(VetoMessage vetoMessage) {
        if (vetoMessage != null) {
            this.billId = new BillIdView(vetoMessage.getBillId());
            this.year = vetoMessage.getYear();
            this.vetoNumber = vetoMessage.getVetoNumber();
            this.memoText = vetoMessage.getMemoText();
            this.vetoType = vetoMessage.getType().name();
            this.chapter = vetoMessage.getChapter();
            this.billPage = vetoMessage.getBillPage();
            this.lineStart = vetoMessage.getLineStart();
            this.lineEnd = vetoMessage.getLineEnd();
            this.signer = vetoMessage.getSigner();
            this.signedDate = vetoMessage.getSignedDate();
        }
    }

    public BillIdView getBillId() {
        return billId;
    }

    public int getYear() {
        return year;
    }

    public int getVetoNumber() {
        return vetoNumber;
    }

    public String getMemoText() {
        return memoText;
    }

    public String getVetoType() {
        return vetoType;
    }

    public int getChapter() {
        return chapter;
    }

    public int getBillPage() {
        return billPage;
    }

    public int getLineStart() {
        return lineStart;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public String getSigner() {
        return signer;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    @Override
    public String getViewType() {
        return "veto-message";
    }
}
