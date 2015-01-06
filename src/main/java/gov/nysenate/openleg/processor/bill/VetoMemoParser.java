package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillTextType;
import gov.nysenate.openleg.model.bill.VetoId;
import gov.nysenate.openleg.model.bill.VetoMessage;
import gov.nysenate.openleg.model.bill.VetoType;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VetoMemoParser extends BillTextParser
{
    private static final Logger logger = LoggerFactory.getLogger(VetoMemoParser.class);

    /** --- RegEx Patterns --- */

    private static final Pattern vetoHeaderPattern =
        Pattern.compile("00000\\.SO DOC VETO(\\d{4})\\s{8}([*A-Z ]{9})[A-Z0-9 ]{16}VETO\\s*(\\d{4})");

    private static final Pattern datePattern =
        Pattern.compile("\\d{5}TO THE (SENATE|ASSEMBLY):\\s*([a-zA-Z]+ \\d+, \\d+)?");

    private static final Pattern chapterPattern =
        Pattern.compile("\\d{5}CHAPTER (\\d+)");

    private static final Pattern lineReferencePattern =
        Pattern.compile("\\d{5}Bill Page (\\d+), Line (\\d+)( through Line (\\d+))?.*");

    private static final Pattern signerPattern =
        Pattern.compile("\\d{5}\\s*(?:(?:The|This|These) bills? (?:is|are) disapproved\\.)?\\s*\\(signed\\) ([a-zA-Z.'\\- ]*[a-zA-Z.])");

    /** A veto message object that is constructed while parsing the veto memo */
    private VetoMessage vetoMessage;

    /** --- Constructors --- */

    public VetoMemoParser (String data, LocalDateTime date) {
        super(data, BillTextType.VETO_APPROVAL , date);
        vetoMessage = new VetoMessage();
        vetoMessage.setType(VetoType.STANDARD);
    }

    /** --- Overrides --- */

    /**
     * {@inheritDoc}
     *
     * Sets the vetoMessage text to the extracted text.
     */
    @Override
    public String extractText() throws ParseError {
        String memoText = super.extractText();
        vetoMessage.setMemoText(memoText);
        return memoText;
    }

    /**
     * {@inheritDoc}
     *
     * Performs additional vetoMessage parsing in addition to the standard bill text parsing
     */
    @Override
    protected String parseLine(String line, StringBuilder text, String fullText) throws ParseError {
        int lineNum = Integer.parseInt(line.substring(0,5));
        if (lineNum == 0) {
            Matcher headerMatcher = vetoHeaderPattern.matcher(line);
            if (headerMatcher.find()) {
                vetoMessage.setVetoNumber(Integer.parseInt(headerMatcher.group(1)));
                vetoMessage.setYear(Integer.parseInt(headerMatcher.group(3)));
                vetoMessage.setSession(new SessionYear(vetoMessage.getYear()));
            }
        }
        else if (lineNum == 4) {
            Matcher dateMatcher = datePattern.matcher(line);
            if (dateMatcher.find()) {
                if (dateMatcher.group(2)==null) { // This date is only present on line vetos
                    vetoMessage.setType(VetoType.STANDARD);
                }
                else {
                    vetoMessage.setType(VetoType.LINE_ITEM);
                    vetoMessage.setSignedDate(LocalDate.parse(dateMatcher.group(2), DateTimeFormatter.ofPattern("MMMM d, yyyy")));
                }
            }
        }
        else if (lineNum == 11 && vetoMessage.getType() == VetoType.LINE_ITEM) {
            Matcher chapterMatcher = chapterPattern.matcher(line);
            if (chapterMatcher.find()) {
                vetoMessage.setChapter(Integer.parseInt(chapterMatcher.group(1)));
            }
        }
        else if (lineNum > 16 && vetoMessage.getType() == VetoType.LINE_ITEM && vetoMessage.getBillPage()==0) {
            Matcher lineReferenceMatcher = lineReferencePattern.matcher(line);
            if (lineReferenceMatcher.find()) {
                vetoMessage.setBillPage(Integer.parseInt(lineReferenceMatcher.group(1)));
                vetoMessage.setLineStart(Integer.parseInt(lineReferenceMatcher.group(2)));
                if (lineReferenceMatcher.group(4) == null) {
                    vetoMessage.setLineEnd(vetoMessage.getLineStart());
                }
                else {
                    vetoMessage.setLineEnd(Integer.parseInt(lineReferenceMatcher.group(4)));
                }
            }
        }
        else if (lineNum > 29 || lineNum > 14 && vetoMessage.getType() == VetoType.STANDARD) {
            Matcher signerMatcher = signerPattern.matcher(line);
            if (signerMatcher.find()) {
                vetoMessage.setSigner(signerMatcher.group(1));
            }
        }
        return super.parseLine(line, text, fullText);
    }

    /** --- Internal --- */

    /**
     * Verifies that all fields of the veto message have been populated
     * BillId is not checked since it is added outside of this class
     * @throws ParseError
     */
    private void verifyVetoMessage() throws ParseError {
        boolean completeVetoMessage =
            vetoMessage.getVetoNumber()!=0 &&
                vetoMessage.getYear()!=0 &&
                vetoMessage.getSession()!=null &&
                vetoMessage.getMemoText()!=null;

        if (vetoMessage.getType() == VetoType.LINE_ITEM) {
            completeVetoMessage = completeVetoMessage &&
                vetoMessage.getSignedDate()!=null &&
                vetoMessage.getChapter()!=0 &&
                vetoMessage.getBillPage()!=0 &&
                vetoMessage.getLineStart()!=0 &&
                vetoMessage.getLineEnd()!=0;
        }
        if (!completeVetoMessage) {
            logger.warn("{}", OutputUtils.toJson(vetoMessage));
            throw new ParseError("End of message reached before all veto information could be extracted");
        }
    }

    /** --- Functional Getters/Setters --- */

    public VetoMessage getVetoMessage() throws ParseError {
        if (isDeleted()) {
            return null;
        }
        verifyVetoMessage();
        return vetoMessage;
    }

    public VetoId getVetoId() {
        if (vetoMessage != null) {
            return vetoMessage.getVetoId();
        } else {
            return null;
        }
    }
}
