package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawFile;

import java.util.List;

public interface LawProcessService
{
    /**
     * Identifies all incoming law files and breaks them down into fragments. The fragments are determined
     * based on the document id header that is used to delineate the law files. Each doc header from each file
     * is associated with a single law fragment. These extracted fragments, as well as the file references, are
     * stored in the database and set to await processing.
     *
     * @return int - Number of law files collated
     */
    public int collateLawFiles();

    /**
     * Retrieve a list of the files that are awaiting processing.
     *
     * @return List<LawFile>
     */
    public List<LawFile> getPendingLawFiles();

    /**
     * Processes the given law files and updates the backing store as necessary.
     *
     * @param lawFiles List<LawFile>
     */
    public void processLawFiles(List<LawFile> lawFiles);

    /**
     * Processes all the law files that are set to await processing.
     */
    public void processPendingLawFiles();
}