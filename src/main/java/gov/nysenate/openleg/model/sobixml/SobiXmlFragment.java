package gov.nysenate.openleg.model.sobixml;

import java.time.LocalDateTime;

/**
 * Represents the data of a SOBI XML file. Any processing code should interact with this class instead
 * of the source SobiXmlFile.
 */
public class SobiXmlFragment
{
    /** Reference to the parent file. */
    private SobiXmlFile xmlFile;

    /** The type of xml fragment. */
    private SobiXmlFragmentType fragmentType;

    /** The publish date is the date specified in the file name. */
    private LocalDateTime publishedDateTime;

    /** The actual data */
    private String xml;

    /** --- Constructors --- */

    public SobiXmlFragment(SobiXmlFile xmlFile, SobiXmlFragmentType fragmentType, LocalDateTime publishedDateTime,
                           String xml) {
        this.xmlFile = xmlFile;
        this.fragmentType = fragmentType;
        this.publishedDateTime = publishedDateTime;
        this.xml = xml;
    }

    /** --- Basic Getters --- */

    public SobiXmlFile getXmlFile() {
        return xmlFile;
    }

    public SobiXmlFragmentType getFragmentType() {
        return fragmentType;
    }

    public LocalDateTime getPublishedDateTime() {
        return publishedDateTime;
    }

    public String getXml() {
        return xml;
    }
}