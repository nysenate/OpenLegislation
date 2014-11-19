package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.processor.base.ProcessService;

import java.util.List;

public interface LawProcessService extends ProcessService
{
    /**
     * Identifies all incoming law files and saves a record of them into the database. The files
     * are set as pending processing so that they are queued for ingestion.
     *
     * @return int - Number of law files collated
     */
    public int collateLawFiles();

    /**
     * Retrieve a list of the files that are awaiting processing.
     *
     * @param limitOffset LimitOffset - Restrict the result set.
     * @return List<LawFile>
     */
    public List<LawFile> getPendingLawFiles(LimitOffset limitOffset);

    /**
     * Processes the given law files and updates the backing store as necessary.
     *
     * @param lawFiles List<LawFile>
     */
    public void processLawFiles(List<LawFile> lawFiles);

    /**
     * Processes all the law files that are set as pending processing.
     */
    public int processPendingLawFiles();
}