package gov.nysenate.openleg.processor.xml;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentNotFoundEx;
import gov.nysenate.openleg.model.sobi.SobiProcessOptions;
import gov.nysenate.openleg.model.xml.XmlFile;
import gov.nysenate.openleg.processor.base.ProcessService;

import java.util.List;

/**
 * The XmlProcessService interface provides the necessary methods for collating
 * and processing xml files. These methods should typically be used via a
 * process intended to parse new xml files.
 */
public interface XmlProcessService extends ProcessService
{

    /**
     * Retrieves the XmlFiles that are awaiting processing.
     *
     * @param sortByPubDate SortOrder - Sort order for the files.
     * @param limitOffset LimitOffset - Restrict the results list.
     * @return List<XmlFile>
     */
    public List<XmlFile> getPendingXmlFiles(SortOrder sortByPubDate, LimitOffset limitOffset);

    /**
     * Process the list of supplied XmlFiles.
     *  @param files - List of fragments to process.
     * @param options - XmlProcessOptions - Provide custom processing options
     */
    public int processXmlFiles(List<XmlFile> files, XmlProcessOptions options);

    /**
     * Toggle the pending processing status of an XmlFile via it's fileId.
     *
     * @param fileId String - The file id
     * @param pendingProcessing boolean - Indicate if fragment is pending processing
     * @throws XmlFragmentNotFoundEx - If the fileId did not match a stored file
     */
    public void updatePendingProcessing(String fileId, boolean pendingProcessing)
            throws XmlFragmentNotFoundEx;
}
