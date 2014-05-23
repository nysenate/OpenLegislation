package gov.nysenate.openleg.model.sobi;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The SOBIFragment class represents a portion of a SOBI file that contains data pertaining
 * to a certain entity type.
 *
 * For example if a SOBI file contains bill data and agenda data, the file can be broken down
 * into two SOBIFragments, one containing the portion for just the bill data and the other with
 * just the agenda data.
 */
public class SOBIFragment
{
    /** Reference to the original SOBI object that created this fragment. */
    private SOBI parentSOBI;

    /** The type of fragment, e.g bill, agenda, etc. */
    private SOBIFragmentType fragmentType;

    /** The file name of the fragment. Serves as a unique identifier for the fragment. */
    private String fileName;

    /** The published datetime of the fragment which is the same as that of the parent SOBI. */
    private Date publishedDateTime;

    /** The datetime when the fragment was processed. */
    private Date processedDateTime;

    /** Reference to the file containing this fragment */
    private File fragmentFile;

    /** The actual text body of the fragment. */
    private String text;

    /** The line number in the parent sobi file where this fragment starts. */
    private int startLineNo;

    /** The line number in the parent sobi file where this fragment end. */
    private int endLineNo;

    /** --- Constructors --- */

    public SOBIFragment(SOBI parentSOBI, SOBIFragmentType fragmentType, File fragmentFile) throws IOException {
        this.parentSOBI = parentSOBI;
        this.fragmentType = fragmentType;
        this.fragmentFile = fragmentFile;
        this.fileName = fragmentFile.getName();
        this.publishedDateTime = parentSOBI.getPublishedDateTime();
        this.text = FileUtils.readFileToString(fragmentFile);
    }

    /** --- Methods --- */

    /**
     * Indicates if given fragment is in the SOBI block format, based on the type.
     * @return boolean
     */
    public boolean isBlockFormat() {
        return fragmentType.equals(SOBIFragmentType.BILL);
    }

    /**
     * Parses the given SOBI fragment into a list of blocks. Replaces null bytes in each line with spaces to
     * bring them into the proper fixed width formats.
     * <p>See the SOBIBlock class for more details.</p>
     * @return List<SOBIBlock> if fragment type supports blocks, empty list otherwise.
     * @throws IOException if fragment file cannot be opened for reading.
     */
    public List<SOBIBlock> getSOBIBlocks() throws IOException {
        List<SOBIBlock> blocks = new ArrayList<>();
        if (isBlockFormat()) {
            SOBIBlock block = null;
            List<String> lines = FileUtils.readLines(fragmentFile);
            lines.add(""); // Add a trailing line to end the last block and remove edge cases

            for(int lineNo = 0; lineNo < lines.size(); lineNo++) {
                // Replace NULL bytes with spaces to properly format lines.
                String line = lines.get(lineNo).replace('\0', ' ');

                // Source file is not assumed to be 100% SOBI so we filter out other lines
                Matcher headerMatcher = SOBIBlock.blockPattern.matcher(line);

                if (headerMatcher.find()) {
                    if (block == null) {
                        // No active block with a new matching line: create new block
                        block = new SOBIBlock(fragmentFile, lineNo, line);
                    }
                    else if (block.getHeader().equals(headerMatcher.group()) && block.isMultiline()) {
                        // active multi-line block with a new matching line: extend block
                        block.extend(line);
                    }
                    else {
                        // active block does not match new line or can't be extended: create new block
                        block.setEndLineNo(lineNo - 1);
                        blocks.add(block);
                        SOBIBlock newBlock = new SOBIBlock(fragmentFile, lineNo, line);

                        // Handle certain SOBI grouping edge cases.
                        if (newBlock.getBillHeader().equals(block.getBillHeader())) {
                            // The law code line can be omitted when blank but it always precedes the 'C' line
                            if (newBlock.getType().equals(SOBILineType.SUMMARY) && !block.getType().equals(SOBILineType.LAW)) {
                                blocks.add(new SOBIBlock(fragmentFile, lineNo, block.getBillHeader()+"B"));
                            }
                        }

                        // Start a new block
                        block = newBlock;
                    }
                }
                else if (block != null) {
                    // Active block with non-matching line: end the current blockAny non-matching line ends the current block
                    block.setEndLineNo(lineNo - 1);
                    blocks.add(block);
                    block = null;
                }
            }
        }
        return blocks;
    }

    /** --- Basic Getters/Setters --- */

    public SOBI getParentSOBI() {
        return parentSOBI;
    }

    public SOBIFragmentType getFragmentType() {
        return fragmentType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getPublishedDateTime() {
        return publishedDateTime;
    }

    public Date getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(Date processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public int getStartLineNo() {
        return startLineNo;
    }

    public void setStartLineNo(int startLineNo) {
        this.startLineNo = startLineNo;
    }

    public int getEndLineNo() {
        return endLineNo;
    }

    public void setEndLineNo(int endLineNo) {
        this.endLineNo = endLineNo;
    }
}