package gov.nysenate.openleg.model.sobi;

import java.util.Date;

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

    /** The actual text body of the fragment. */
    private String text;

    /** --- Constructors --- */

    public SOBIFragment(SOBI parentSOBI, SOBIFragmentType fragmentType, String fileName) {
        this.parentSOBI = parentSOBI;
        this.fragmentType = fragmentType;
        this.fileName = fileName;
        this.publishedDateTime = parentSOBI.getPublishedDateTime();
    }

    /** --- Basic Getters/Setters --- */

    public SOBI getParentSOBI() {
        return parentSOBI;
    }

    public void setParentSOBI(SOBI parentSOBI) {
        this.parentSOBI = parentSOBI;
    }

    public SOBIFragmentType getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(SOBIFragmentType fragmentType) {
        this.fragmentType = fragmentType;
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
}