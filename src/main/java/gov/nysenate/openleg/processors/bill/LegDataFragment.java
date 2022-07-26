package gov.nysenate.openleg.processors.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.processors.BaseSourceData;
import gov.nysenate.openleg.processors.bill.sobi.SobiBlock;
import gov.nysenate.openleg.processors.bill.sobi.SobiLineType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The LegDataFragment class represents a portion of a Source file that contains data pertaining
 * to a certain entity type (Bill, Calendar, etc).
 *
 * For example if a SOBI file contains bill data and agenda data, the file can be broken down
 * into two SOBIFragments, one containing the portion for just the bill data and the other with
 * just the agenda data.
 */
public class LegDataFragment extends BaseSourceData {
    /** Reference to the original SobiFile object that created this fragment. */
    private final SourceFile parentSourceFile;

    /** The type of fragment, e.g bill, agenda, etc. */
    private final LegDataFragmentType type;

    /** The unique id of the fragment which is derived from the other fields.
     *  The fragmentId is created such that it can be used for sorting. */
    private final String fragmentId;

    /** A number used to provide a means of ordering fragments within the same SobiFile. */
    private final int sequenceNo;

    /** The actual text body of the fragment. */
    private final String text;

    /** The last time processing was initiated for the fragment */
    private LocalDateTime processStartDateTime = null;

    /** --- Constructors --- */

    public LegDataFragment(SourceFile parentSourceFile, LegDataFragmentType type, String text, int sequenceNo) {
        this(generateFragmentId(parentSourceFile, type, sequenceNo),
                parentSourceFile, type, text, sequenceNo);
    }

    public LegDataFragment(String fragmentId, SourceFile parentSourceFile, LegDataFragmentType type, String text, int sequenceNo) {
        this.fragmentId = fragmentId;
        this.parentSourceFile = parentSourceFile;
        this.type = type;
        this.text = text;
        this.sequenceNo = sequenceNo;
    }

    /* --- Methods --- */

    /**
     * Indicates if given fragment is in the SOBI block format, based on the type.
     * @return boolean
     */
    public boolean isBlockFormat() {
        return type.equals(LegDataFragmentType.BILL);
    }

    /**
     * Parses the given Sobi fragment into a list of blocks if it's in block format.
     * Replaces null bytes in each line with spaces to bring them into the proper fixed width formats.
     *
     * @return List<SobiBlock> if fragment type supports blocks, empty list otherwise.
     */
    @JsonIgnore
    public List<SobiBlock> getSobiBlocks() {
        List<SobiBlock> blocks = new ArrayList<>();
        if (!isBlockFormat()) {
            return blocks;
        }

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
                    block = new SobiBlock(fragmentId, type, getPublishedDateTime(), lineNo, line);
                }
                else if (block.getHeader().equals(headerMatcher.group()) && block.isMultiline()) {
                    // Active multi-line block with a new matching line: extend block
                    block.extend(line);
                }
                else {
                    // Active block does not match new line or can't be extended: create new block
                    block.setEndLineNo(lineNo - 1);
                    blocks.add(block);
                    SobiBlock newBlock = new SobiBlock(fragmentId, type, getPublishedDateTime(), lineNo, line);
                    // Handle certain SOBI grouping edge cases.
                    if (newBlock.getBillHeader().equals(block.getBillHeader())) {
                        // The law code line can be omitted when blank but it always precedes the 'C' line
                        if (newBlock.getType().equals(SobiLineType.SUMMARY) && !block.getType().equals(SobiLineType.LAW)) {
                            blocks.add(new SobiBlock(fragmentId, type, getPublishedDateTime(), lineNo,
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
        return blocks;
    }

    /**
     * Creates a unique fragment id using the given parameters.
     */
    public static String generateFragmentId(SourceFile sf, LegDataFragmentType type, int sequenceNo) {
        return String.format("%s-%d-%s", sf.getFileName(), sequenceNo, type.name());
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "LegDataFragment{" + "fragmentType=" + type + ", fileName='" + fragmentId + '\'' +
                ", parentSobiFile=" + parentSourceFile + '}';
    }

    /** --- Functional Getters/Setters --- */

    public LocalDateTime getPublishedDateTime() {
        return parentSourceFile.getPublishedDateTime();
    }

    public void startProcessing() {
        this.processStartDateTime = LocalDateTime.now();
    }

    /** --- Basic Getters/Setters --- */

    public SourceFile getParentLegDataFile() {
        return parentSourceFile;
    }

    public LegDataFragmentType getType() {
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

    public LocalDateTime getProcessStartDateTime() {
        return processStartDateTime;
    }

    public void setProcessStartDateTime(LocalDateTime processStartDateTime) {
        this.processStartDateTime = processStartDateTime;
    }
}