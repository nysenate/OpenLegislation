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
    private SOBI parentSOBI;

    private SOBIFragmentType fragmentType;

    private String fileName;

    private String text;

    private Date publishedDateTime;

    private Date processedDateTime;

    public SOBIFragment(SOBI parentSOBI, SOBIFragmentType fragmentType, String fileName) {
        this.parentSOBI = parentSOBI;
        this.fragmentType = fragmentType;
        this.fileName = fileName;
        this.publishedDateTime = parentSOBI.getPublishedDateTime();
    }

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