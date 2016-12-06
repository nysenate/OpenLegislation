package gov.nysenate.openleg.processor.xml;

import gov.nysenate.openleg.model.xml.XmlFile;
import gov.nysenate.openleg.model.xml.XmlFileType;

/**
 * Specific xml file type processors (i.e. BillTextXmlProcessor) will implement this interface
 */
public interface XmlProcessor {

    /**
     * Returns the supported type of xml file for this processor
     */
    public XmlFileType getSupportedType();

    /**
     * Processes the given file (as long as it matched the supported type)
     */
    public void process(final XmlFile file);

    /**
     * Perform any additional tasks that must be run prior to finishing processing.
     */
    public void postProcess();
}
