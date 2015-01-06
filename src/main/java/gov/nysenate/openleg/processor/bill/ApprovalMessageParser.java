package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillTextType;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.processor.base.ParseError;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApprovalMessageParser extends BillTextParser {

    /** --- Regex Patterns --- */

    private static final Pattern approvalHeaderPattern =
            Pattern.compile("00000\\.SO DOC APPR(\\d+)\\s+APPROVAL\\s+(\\d{4})");
    private static final Pattern approvalTitlePattern =
            Pattern.compile("\\d{5}\\s+APPROVAL MEMORANDUM\\s+-\\s+No\\.\\s+\\d+\\s+Chapter\\s+(\\d+)");
    private static final Pattern approvalBillIdPattern =
            Pattern.compile("(?i)\\d{5}\\s+MEMORANDUM filed with (Senate|Assembly) Bill Number (\\d+)\\-?([A-Z])?, entitled:");
    private static final Pattern approvalSignerPattern =
            Pattern.compile("\\d{5}\\s+(?:(?:The|This) bill is|These bills are) approved\\.\\s*\\(signed\\)\\s*([a-zA-Z.'\\- ]*[a-zA-Z.])");

    /** An approval message object that is constructed while parsing the memo*/
    private ApprovalMessage approvalMessage;

    /** --- Constructors --- */

    public ApprovalMessageParser(String data, LocalDateTime dateTime) {
        super(data, BillTextType.VETO_APPROVAL, dateTime);
        approvalMessage = new ApprovalMessage();
    }

    /** --- Functional getters/setters --- */

    /**
     * Verifies the approval message before returning it
     * @return
     * @throws ParseError
     */
    public ApprovalMessage getApprovalMessage() throws ParseError{
        if (isDeleted()) {
            return null;
        }
        verifyApprovalMessage();
        return approvalMessage;
    }

    public ApprovalId getApprovalId() {
        if (approvalMessage != null) {
            return approvalMessage.getApprovalId();
        } else {
            return null;
        }
    }

    /** --- Overrides --- */

    /**
     * {@inheritDoc}
     * Sets the approval message text to the memo text
     * @return
     * @throws ParseError
     */
    @Override
    public String extractText() throws ParseError {
        String memoText = super.extractText();
        approvalMessage.setMemoText(memoText);
        return memoText;
    }

    /**
     * {@inheritDoc}
     * Performs additional approval message parsing to extract meta data
     * @param line
     * @param text
     * @param fullText
     * @return
     * @throws ParseError
     */
    @Override
    protected String parseLine(String line, StringBuilder text, String fullText) throws ParseError {
        int lineNum = Integer.parseInt(line.substring(0,5));
        if(lineNum == 0) {
            Matcher headerMatcher = approvalHeaderPattern.matcher(line);
            if(headerMatcher.matches()){
                approvalMessage.setApprovalNumber(Integer.parseInt(headerMatcher.group(1)));
                approvalMessage.setYear(Integer.parseInt(headerMatcher.group(2)));
                approvalMessage.setSession(new SessionYear(approvalMessage.getYear()));
            }
        }
        else if (lineNum == 2) {
            Matcher titleMatcher = approvalTitlePattern.matcher(line);
            if(titleMatcher.matches()){
                approvalMessage.setChapter(Integer.parseInt(titleMatcher.group(1)));
            }
        }
        else if (lineNum > 11 && approvalMessage.getSigner()==null){
            Matcher signerMatcher = approvalSignerPattern.matcher(line);
            if(signerMatcher.matches()) {
                approvalMessage.setSigner(signerMatcher.group(1));
            }
        }
        return super.parseLine(line, text, fullText);
    }

    /** --- Internal Methods --- */

    /**
     * Checks that all necessary fields are present for the approval message
     * @throws ParseError if fields are missing
     */
    private void verifyApprovalMessage() throws ParseError {
        boolean completeApprovalMessage =
            approvalMessage.getApprovalNumber()!=0 &&
            approvalMessage.getYear()!=0 &&
            approvalMessage.getSession()!=null &&
            approvalMessage.getChapter()!=0;
        if (!completeApprovalMessage) {
            throw new ParseError("Incomplete approval message");
        }
    }


}
