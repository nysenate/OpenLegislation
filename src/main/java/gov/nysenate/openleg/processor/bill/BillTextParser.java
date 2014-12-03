package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.BillTextType;
import gov.nysenate.openleg.processor.base.ParseError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillTextParser
{
    private static final Pattern textHeaderPattern =
        Pattern.compile("00000\\.SO DOC.{17}([A-Z* ]{9})[A-Z0-9 ]{16}([A-Z ]{20}) ([0-9]{4}).*");

    private String data;
    private BillTextType billTextType;
    private LocalDateTime dateTime;

    /** This is set as true when the parser has received a valid text header
     *  and has not yet received a closing header */
    private boolean insideTextHeader;

    /** A flag that is set to true when the parser detects a delete statement */
    private boolean deleted = false;

    public BillTextParser(String data, BillTextType billTextType, LocalDateTime dateTime) {
        this.data = data;
        this.billTextType = billTextType;
        this.dateTime = dateTime;
        this.insideTextHeader = false;
    }

    /**
     * Applies information to bill text or memo; replaces any existing information.
     * Header lines start with 00000.SO DOC and contain one of three actions:
     *
     * '' - Start of the bill text</li>
     * *END* - End of the bill text</li>
     * *DELETE* - Deletes existing bill text</li>
     *
     * Examples
     * -----------------------------------------------------------------------------------------------------
     * Resolution Text | R00000.SO DOC A R22                                    RESO TEXT            2013
     *                 | R00001LEGISLATIVE  RESOLUTION  congratulating  the Maine-Endwell Football Team
     *                 | R00000.SO DOC A R22           *END*                    RESO TEXT            2013
     * -----------------------------------------------------------------------------------------------------
     * Bill Text       | T00000.SO DOC S 53                                     BTXT                 2013
     *                 | T00002                           S T A T E   O F   N E W   Y O R K
     *                 | T00000.SO DOC S 53            *END*                    BTXT                 2013
     * -----------------------------------------------------------------------------------------------------
     * Memo Text       | M00000.SO DOC S 1626                                   MTXT                 2013
     *                 | M00006PURPOSE OR GENERAL IDEA OF BILL:  The purpose of this bill is to
     *                 | M00000.SO DOC S 1625          *END*                    MTXT                 2013
     * -----------------------------------------------------------------------------------------------------
     * Delete          | T00000.SO DOC A 8396          *DELETE*                 BTXT                 2013
     * -----------------------------------------------------------------------------------------------------
     *
     * @throws ParseError
     */
    public String extractText() throws ParseError {
        // BillText, ResolutionText, and MemoText can be handled the same way.
        // Since Text Blocks can be back to back we constantly look for headers
        // with actions that tell us to start over, end, or delete.
        StringBuilder text = new StringBuilder();
        text.ensureCapacity(data.length());
        String fullText = "";

        for (String line : data.split("\n")) {
            fullText = parseLine(line, text, fullText);
        }
        if (insideTextHeader) {
            // This is a known issue that was resolved on 03/23/2011
            if (dateTime.isAfter(LocalDate.of(2011, 3, 23).atStartOfDay())) {
                throw new ParseError("Finished text data without a footer");
            }
            else {
                // Commit what we have and move on
                fullText = text.toString();
            }
        }
        return fullText;
    }

    /**
     * Performs parsing actions for a line of the bill text
     * @param line
     */
    protected String parseLine(String line, StringBuilder text, String fullText) throws ParseError {
        Matcher header = textHeaderPattern.matcher(line);
        if (line.startsWith("00000") && header.find()) {
            String action = header.group(1).trim();
            String type = header.group(2).trim();
            if (!type.matches(billTextType.getTypeString())) {
                throw new ParseError("Unknown text type found: " + type);
            }
            switch (action) {
                case "*DELETE*":
                    text.setLength(0);
                    insideTextHeader = false;
                    this.deleted = true;
                    break;
                case "*END*":
                    if (insideTextHeader) {
                        this.deleted = false;
                        fullText = text.toString();
                        text.setLength(0);
                        insideTextHeader = false;
                    }
                    else {
                        throw new ParseError("Text END Found before a body: " + line);
                    }
                    break;
                case "": // No action indicates the start of a text header
                    // This header repeats every 100 lines
                    insideTextHeader = true;
                    break;
                default:
                    throw new ParseError("Unrecognized action type: " + line);
            }
        }
        else if (insideTextHeader) {
            // Remove the leading numbers
            text.append((line.length() > 5) ? line.substring(5) : line.substring(line.length()));
            text.append("\n");
        }
        else {
            throw new ParseError("Text Body found before header: "+line);
        }
        return fullText;
    }

    /** Basic Getters / Setters **/

    public boolean isDeleted() {
        return deleted;
    }
}
