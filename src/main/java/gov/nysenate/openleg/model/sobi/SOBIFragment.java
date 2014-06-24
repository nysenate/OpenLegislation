package gov.nysenate.openleg.model.sobi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The SOBIFragment class represents a portion of a SOBIFile file that contains data pertaining
 * to a certain entity type.
 *
 * For example if a SOBI file contains bill data and agenda data, the file can be broken down
 * into two SOBIFragments, one containing the portion for just the bill data and the other with
 * just the agenda data.
 */
public class SOBIFragment
{
    /** Reference to the original SOBIFile object that created this fragment. */
    private SOBIFile parentSOBIFile;

    /** The type of fragment, e.g bill, agenda, etc. */
    private SOBIFragmentType type;

    /** The file name of the fragment. Serves as a unique identifier for the fragment. */
    private String fileName;

    /** A counter used to provide a means of ordering multiple fragments of the same type. */
    private int counter;

    /** The actual text body of the fragment. */
    private String text;

    /** --- Constructors --- */

    public SOBIFragment(SOBIFile parentSOBIFile, SOBIFragmentType type, String text, int counter) {
        this.parentSOBIFile = parentSOBIFile;
        this.type = type;
        this.text = text;
        this.counter = counter;
    }

    /** --- Methods --- */

    /**
     * Indicates if given fragment is in the SOBI block format, based on the type.
     * @return boolean
     */
    public boolean isBlockFormat() {
        return type.equals(SOBIFragmentType.BILL);
    }

    /**
     * Parses the given SOBI fragment into a list of blocks. Replaces null bytes in each line with spaces to
     * bring them into the proper fixed width formats.
     * <p>See the SOBIBlock class for more details.</p>
     * @return List<SOBIBlock> if fragment type supports blocks, empty list otherwise.
     */
    public List<SOBIBlock> getSOBIBlocks() {
        List<SOBIBlock> blocks = new ArrayList<>();
        if (isBlockFormat()) {
            SOBIBlock block = null;
            List<String> lines = new ArrayList<>(Arrays.asList(this.text.split("\\r?\\n")));
            lines.add(""); // Add a trailing line to end the last block and remove edge cases
            for(int lineNo = 0; lineNo < lines.size(); lineNo++) {
                // Replace NULL bytes with spaces to properly format lines.
                String line = lines.get(lineNo).replace('\0', ' ');
                // Source file is not assumed to be 100% SOBI so we filter out other lines
                Matcher headerMatcher = SOBIBlock.blockPattern.matcher(line);
                if (headerMatcher.find()) {
                    if (block == null) {
                        // No active block with a new matching line: create new block
                        block = new SOBIBlock(fileName, type, lineNo, line);
                    }
                    else if (block.getHeader().equals(headerMatcher.group()) && block.isMultiline()) {
                        // active multi-line block with a new matching line: extend block
                        block.extend(line);
                    }
                    else {
                        // active block does not match new line or can't be extended: create new block
                        block.setEndLineNo(lineNo - 1);
                        blocks.add(block);
                        SOBIBlock newBlock = new SOBIBlock(fileName, type, lineNo, line);
                        // Handle certain SOBI grouping edge cases.
                        if (newBlock.getBillHeader().equals(block.getBillHeader())) {
                            // The law code line can be omitted when blank but it always precedes the 'C' line
                            if (newBlock.getType().equals(SOBILineType.SUMMARY) && !block.getType().equals(SOBILineType.LAW)) {
                                blocks.add(new SOBIBlock(fileName, type, lineNo,
                                           block.getBillHeader() + SOBILineType.LAW.getTypeCode()));
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

    @Override
    public String toString() {
        return "SOBIFragment{" + "fragmentType=" + type + ", fileName='" + fileName + '\'' +
                ", parentSOBIFile=" + parentSOBIFile + '}';
    }

    /** --= Functional Getters/Setters --- */

    public Date getPublishedDateTime() {
        return parentSOBIFile.getPublishedDateTime();
    }

    public Date getProcessedDateTime() {
        return parentSOBIFile.getProcessedDateTime();
    }

    /** --- Basic Getters/Setters --- */

    public SOBIFile getParentSOBIFile() {
        return parentSOBIFile;
    }

    public SOBIFragmentType getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getText() {
        return text;
    }

    public int getCounter() {
        return counter;
    }
}