package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.legislation.bill.ApprovalMessage;
import gov.nysenate.openleg.legislation.bill.BillId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by senateuser on 3/13/17.
 */
public class XmlApprovalMessageParser {
    private String memoText;
    private BillId billId;
    private int apprno;
    ApprovalMessage approvalMessage;
    private static final Pattern approvalTitlePattern =
            Pattern.compile("\\s+APPROVAL MEMORANDUM\\s+-\\s+No\\.\\s?+\\d+\\s+Chapter\\s+?(\\d+)");
    private static final Pattern approvalSignerPattern =
            Pattern.compile("\\s+(?:(?:The|This) bill is|These bills are) approved\\.\\s*\\(signed\\)\\s*([a-zA-Z.'\\- ]*[a-zA-Z.])");

    public XmlApprovalMessageParser(String text, BillId billId, int apprno) {
        this.memoText = text;
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
        if (!matt.find()) { // noone sign
            return "";
        }
        return matt.group(1);
    }
}
