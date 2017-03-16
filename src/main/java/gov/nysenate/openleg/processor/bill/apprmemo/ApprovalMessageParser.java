package gov.nysenate.openleg.processor.bill.apprmemo;

import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.bill.BillTextParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by senateuser on 3/13/17.
 */
public class ApprovalMessageParser {
    private String memoText;
    private BillId billId;
    private int apprno;
    private String action;
    ApprovalMessage approvalMessage;
    private static final Pattern approvalTitlePattern =
            Pattern.compile("\\s+APPROVAL MEMORANDUM\\s+-\\s+No\\.\\s+\\d+\\s+Chapter\\s+?(\\d+)");
    private static final Pattern approvalSignerPattern =
            Pattern.compile("\\s+(?:(?:The|This) bill is|These bills are) approved\\.\\s*\\(signed\\)\\s*([a-zA-Z.'\\- ]*[a-zA-Z.])");

    public ApprovalMessageParser(String text, BillId billId, int apprno, String action) {
        this.memoText = text;
        this.action = action;
        this.billId = billId;
        this.apprno = apprno;
        approvalMessage = new ApprovalMessage();
    }

    public void extractText() {
        approvalMessage.setBillId(billId);
        approvalMessage.setMemoText(memoText);
        approvalMessage.setApprovalNumber(apprno);
        approvalMessage.setChapter(getChapter());
        approvalMessage.setSigner(getSigner());
    }

    public ApprovalMessage getApprovalMessage() {
        return approvalMessage;
    }

    private int getChapter() {
        Matcher matt = approvalTitlePattern.matcher(memoText);
        matt.find();
        String header = matt.group(1);
        return Integer.parseInt(header);
    }

    private String getSigner() {
        Matcher matt = approvalSignerPattern.matcher(memoText);
        if (!matt.find()) {
            throw new ParseError("No Signature found with the Approval Memorandum.");
        }
        return matt.group(1);
    }
}
