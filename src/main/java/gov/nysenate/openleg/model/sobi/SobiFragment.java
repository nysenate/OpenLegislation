package gov.nysenate.openleg.model.sobi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.model.base.BaseSourceData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The SobiFragment class represents a portion of a SobiFile file that contains data pertaining
 * to a certain entity type (Bill, Calendar, etc).
 *
 * For example if a SOBI file contains bill data and agenda data, the file can be broken down
 * into two SOBIFragments, one containing the portion for just the bill data and the other with
 * just the agenda data.
 */
public class SobiFragment extends BaseSourceData
{
    /** Reference to the original SobiFile object that created this fragment. */
    private SobiFile parentSobiFile;

    /** The type of fragment, e.g bill, agenda, etc. */
    private SobiFragmentType type;

    /** The unique id of the fragment which is derived from the other fields.
     *  The fragmentId is created such that it can be used for sorting. */
    private String fragmentId;

    /** A number used to provide a means of ordering fragments within the same SobiFile. */
    private int sequenceNo;

    /** The actual text body of the fragment. */
    private String text;

    /** --- Constructors --- */

    public SobiFragment(SobiFile parentSobiFile, SobiFragmentType type, String text, int sequenceNo) {
        this(generateFragmentId(parentSobiFile, type, sequenceNo),
             parentSobiFile, type, text, sequenceNo);
    }

    public SobiFragment(String fragmentId, SobiFile parentSobiFile, SobiFragmentType type, String text, int sequenceNo) {
        this.fragmentId = fragmentId;
        this.parentSobiFile = parentSobiFile;
        this.type = type;
        this.text = text;
        this.sequenceNo = sequenceNo;
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
     * Parses the given Sobi fragment into a list of blocks if it's in block format.
     * Replaces null bytes in each line with spaces to bring them into the proper fixed width formats.
     *
     * @see gov.nysenate.openleg.model.sobi.SobiBlock
     * @return List<SobiBlock> if fragment type supports blocks, empty list otherwise.
     */
    @JsonIgnore
    public List<SobiBlock> getSobiBlocks() {
        List<SobiBlock> blocks = new ArrayList<>();
        if (isBlockFormat()) {
            SobiBlock block = null;
            List<String> lines = new ArrayList<>(Arrays.asList(this.text.split("\\r?\\n")));
            lines.add(""); // Add a trailing line to end the last block and remove edge cases
            for (int lineNo = 0; lineNo < lines.size(); lineNo++) {
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
                        // Active multi-line block with a new matching line: extend block
                        block.extend(line);
                    }
                    else {
                        // Active block does not match new line or can't be extended: create new block
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
                    // Active block with non-matching line: end the current block. Any non-matching line ends the current block
                    block.setEndLineNo(lineNo - 1);
                    blocks.add(block);
                    block = null;
                }
            }
        }
        return blocks;
    }

    /**
     * Creates a unique fragment id using the given parameters.
     */
    public static String generateFragmentId(SobiFile sf, SobiFragmentType type, int sequenceNo) {
        return String.format("%s-%d-%s", sf.getFileName(), sequenceNo, type.name());
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "SobiFragment{" + "fragmentType=" + type + ", fileName='" + fragmentId + '\'' +
                ", parentSobiFile=" + parentSobiFile + '}';
    }

    /** --- Functional Getters/Setters --- */

    public LocalDateTime getPublishedDateTime() {
        return parentSobiFile.getPublishedDateTime();
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

    public String getText() {
        return text;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }
}