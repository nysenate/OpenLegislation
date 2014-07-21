package gov.nysenate.openleg.model.sobi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The SobiFragment class represents a portion of a SobiFile file that contains data pertaining
 * to a certain entity type.
 *
 * For example if a SOBI file contains bill data and agenda data, the file can be broken down
 * into two SOBIFragments, one containing the portion for just the bill data and the other with
 * just the agenda data.
 */
public class SobiFragment
{
    /** Reference to the original SobiFile object that created this fragment. */
    private SobiFile parentSobiFile;

    /** The type of fragment, e.g bill, agenda, etc. */
    private SobiFragmentType type;

    /** The id of the fragment which is set by the collate process. */
    private String fragmentId;

    /** A counter used to provide a means of ordering multiple fragments of the same type. */
    private int counter;

    /** The actual text body of the fragment. */
    private String text;

    /** --- Constructors --- */

    public SobiFragment(SobiFile parentSobiFile, SobiFragmentType type, String text, int counter) {
        this.parentSobiFile = parentSobiFile;
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
        return type.equals(SobiFragmentType.BILL);
    }

    /**
     * Parses the given SOBI fragment into a list of blocks. Replaces null bytes in each line with spaces to
     * bring them into the proper fixed width formats.
     * <p>See the SobiBlock class for more details.</p>
     * @return List<SobiBlock> if fragment type supports blocks, empty list otherwise.
     */
    public List<SobiBlock> getSOBIBlocks() {
        List<SobiBlock> blocks = new ArrayList<>();
        if (isBlockFormat()) {
            SobiBlock block = null;
            List<String> lines = new ArrayList<>(Arrays.asList(this.text.split("\\r?\\n")));
            lines.add(""); // Add a trailing line to end the last block and remove edge cases
            for(int lineNo = 0; lineNo < lines.size(); lineNo++) {
                // Replace NULL bytes with spaces to properly format lines.
                String line = lines.get(lineNo).replace('\0', ' ');
                // Source file is not assumed to be 100% SOBI so we filter out other lines
                Matcher headerMatcher = SobiBlock.blockPattern.matcher(line);
                if (headerMatcher.find()) {
                    if (block == null) {
                        // No active block with a new matching line: create new block
                        block = new SobiBlock(fragmentId, type, lineNo, line);
                    }
                    else if (block.getHeader().equals(headerMatcher.group()) && block.isMultiline()) {
                        // active multi-line block with a new matching line: extend block
                        block.extend(line);
                    }
                    else {
                        // active block does not match new line or can't be extended: create new block
                        block.setEndLineNo(lineNo - 1);
                        blocks.add(block);
                        SobiBlock newBlock = new SobiBlock(fragmentId, type, lineNo, line);
                        // Handle certain SOBI grouping edge cases.
                        if (newBlock.getBillHeader().equals(block.getBillHeader())) {
                            // The law code line can be omitted when blank but it always precedes the 'C' line
                            if (newBlock.getType().equals(SobiLineType.SUMMARY) && !block.getType().equals(SobiLineType.LAW)) {
                                blocks.add(new SobiBlock(fragmentId, type, lineNo,
                                           block.getBillHeader() + SobiLineType.LAW.getTypeCode()));
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
        return "SobiFragment{" + "fragmentType=" + type + ", fileName='" + fragmentId + '\'' +
                ", parentSobiFile=" + parentSobiFile + '}';
    }

    /** --= Functional Getters/Setters --- */

    public Date getPublishedDateTime() {
        return parentSobiFile.getPublishedDateTime();
    }

    public Date getProcessedDateTime() {
        return parentSobiFile.getProcessedDateTime();
    }

    /** --- Basic Getters/Setters --- */

    public SobiFile getParentSobiFile() {
        return parentSobiFile;
    }

    public SobiFragmentType getType() {
        return type;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getText() {
        return text;
    }

    public int getCounter() {
        return counter;
    }
}