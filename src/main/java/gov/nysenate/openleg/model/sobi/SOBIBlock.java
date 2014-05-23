package gov.nysenate.openleg.model.sobi;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The SOBIBlock class represents a set of SOBI lines sharing the same header:
 * <p>
 * <pre>
 *     2013A03006D3Amends various provisions of law relating to implementing the health and mental hygiene budget for
 *     2013A03006D3the 2013-2014 state fiscal year.
 * </pre>
 * All of the different header components are parsed out and data is aggregated:
 * <p>
 * <ul>
 *   <li>year: 2013</li>
 *   <li>printNo: "A03006"</li>
 *   <li>amendment:  "D"</li>
 *   <li>type: '3'</li>
 *   <li>data: Amends various provisions of law relating to implementing the health and mental hygiene budget for
 *           the 2013-2014 state fiscal year.</li>
 * </ul>
 * <p>
 * Certain block types and block content should always be single lined. Specifically type 1, 2, and 5 blocks as well
 * as any blocks having "DELETE" as data. A block should always be checked for isMultiline before being extended.
 *
 * @author GraylinKim
 *
 */
public class SOBIBlock
{
    /** A list of sobi line types that are *single* line blocks. All other block types are multi-line. */
    public static final List<SOBILineType> oneLineBlocks =
        Arrays.asList(SOBILineType.BILL_INFO, SOBILineType.LAW_SECTION, SOBILineType.SAME_AS);

    /** A pattern to verify that a string is in the SOBI line format. */
    public static final Pattern blockPattern = Pattern.compile("^[0-9]{4}[A-Z][0-9]{5}[ A-Z][1-9ABCMRTV]");

    /** The line number that the block starts at. Defaults to zero when using the basic constructor. */
    private Integer startLineNo = 0;

    /** The line number that the block ends at. Defaults to zero when using the basic constructor. */
    private Integer endLineNo = 0;

    /** The file from which the block was found. Defaults to null when using the basic constructor. */
    private File file = null;

    /** The full line header from the line used to construct the Block. */
    private String header = "";

    /** The portion of the header containing just the bill designator, i.e not including line type. */
    private String billHeader = "";

    /** The year indicated in the block header. */
    private Integer year = 0;

    /** The printNo of the base bill. The amendment character is NOT included. */
    private String printNo = "";

    /** The amendment character for the base bill. Can be empty or single letter string from "A-Z" */
    private String amendment = "";

    /** The sobi line type of the block. Determines how the data is interpreted. */
    private SOBILineType type;

    /** An internal buffer used to accumulate block data over several lines. */
    private StringBuffer dataBuffer = new StringBuffer();

    /** True if the block should be extended by multiple lines. This is generally determined by block type
     *  but blocks whose data is DELETE should be treated as single line blocks regardless of type. */
    private final boolean multiline;

    /** --- Constructors --- */

    /**
     * Construct a new block with without location information from a valid SOBI line. The line is
     * assumed to be valid SOBI file and is NOT checked for performance reasons.
     */
    public SOBIBlock(String line) {
        this.setYear(Integer.parseInt(line.substring(0,4)));
        this.setPrintNo(line.substring(4,10));
        this.setAmendment(line.substring(10,11).trim());
        this.setBillHeader(line.substring(0, 11));
        this.setType(SOBILineType.valueOfCode(line.charAt(11)));
        this.setHeader(line.substring(0,12));
        this.setData(line.substring(12));
        this.multiline = !oneLineBlocks.contains(this.getType()) && !this.getData().trim().equals("DELETE");
    }

    /**
     * Construct a new block with location information from a valid SOBI line. Holds references to
     * the source file and line number the block was initialized from. The line is assumed to be
     * valid SOBI file and is NOT checked for performance reasons.
     */
    public SOBIBlock(File file, int startLineNo, String line) {
        this(line);
        this.setFile(file);
        this.setStartLineNo(startLineNo);
    }

    /** --- Methods --- */

    /**
     * Extends the block data with the data from the new line. Separates new lines with a '\n' character so
     * that line breaks information is available to downstream parsers.
     *
     * @throws RuntimeException - when attempting to extend a block that shouldn't be extended. Use isMultiline
     * to check before extending.
     */
    public void extend(String line) {
        if (!this.isMultiline())
            throw new RuntimeException("Only multi-line blocks may be extended");
        this.dataBuffer.append("\n"+line.substring(12));
    }

    /**
     * Blocks are considered equal if their header and data (trimmed of all excess whitespace) are
     * identical in content (case-sensitive).
     */
    public boolean equals(Object obj) {
        return obj!= null && obj instanceof SOBIBlock
           && ((SOBIBlock)obj).getHeader().equals(this.getHeader())
           && ((SOBIBlock)obj).getData().trim().equals(this.getData().trim());
    }

    public String toString() {
        return this.getLocation()+":"+this.getHeader();
    }

    /** --- Functional Getters/Setters */

    /**
     * Returns a representation of the location of the block: fileName:lineNumber.
     */
    public String getLocation() {
        return (this.getFile() == null ? "null" : this.getFile().getName())+":"+this.getStartLineNo();
    }

    /**
     * Gets the string representation of the block's data.
     */
    public String getData() {
        return dataBuffer.toString();
    }

    /**
     * Replaces the block data with the input string.
     */
    public void setData(String data) {
        this.dataBuffer = new StringBuffer(data);
    }

    /**
     * Strips all leading zeros from the numerical part of the print number:
     * <p>
     * A00370 -> A370
     *
     * @param printNo
     * @throws NumberFormatException on malformed print numbers
     */
    public void setPrintNo(String printNo) {
        // Integer conversion removes leading zeros in the print number.
        this.printNo = printNo.substring(0,1)+Integer.parseInt(printNo.substring(1));
    }

    /**
     * Sets the end line number ensuring that it is >= 0.
     * @param endLineNo Integer
     */
    public void setEndLineNo(Integer endLineNo) {
        if (endLineNo < 0) {
            endLineNo = 0;
        }
        this.endLineNo = endLineNo;
    }

    /** --- Basic Getters/Setters */

    public int getStartLineNo() {
        return startLineNo;
    }

    public void setStartLineNo(int startLineNo) {
        this.startLineNo = startLineNo;
    }

    public Integer getEndLineNo() {
        return endLineNo;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPrintNo() {
        return printNo;
    }

    public String getAmendment() {
        return amendment;
    }

    public void setAmendment(String amendment) {
        this.amendment = amendment;
    }

    public SOBILineType getType() {
        return type;
    }

    public void setType(SOBILineType type) {
        this.type = type;
    }

    public String getBillHeader() {
        return billHeader;
    }

    public void setBillHeader(String billHeader) {
        this.billHeader = billHeader;
    }

    public boolean isMultiline() {
        return multiline;
    }
}
