package gov.nysenate.openleg.spotchecks.scraping.lrs.bill;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by kyle on 2/19/15.
 */
public interface BillScrapeReferenceDao {


    /**
     * Saves a file containing the scraped bill content to the incoming directory
     *
     * @param content The content of the file.
     * @param scrapedBill The BaseBillId the file represents.
     * @throws IOException
     */
    void saveScrapedBillContent(String content, BaseBillId scrapedBill) throws IOException;

    /**
     * Scans the incoming scraped bill directory and registers any files in the db if they aren't already.
     *
     * @return {@link List<BillScrapeFile>} list of new files registered.
     * @throws IOException
     */
    List<BillScrapeFile> registerIncomingScrapedBills() throws IOException;

    /**
     * @return A list of all incoming scraped bill files
     */
    List<BillScrapeFile> getIncomingScrapedBills() throws IOException;

    /**
     * Moves a scraped bill file into the archive directory and marks as archived in the database.
     * @param scrapedBill
     * @return The updated BillScrapeFile.
     * @throws IOException
     */
    BillScrapeFile archiveScrapedBill(BillScrapeFile scrapedBill) throws IOException;

    /**
     * Updates or inserts a scraped bill file.
     * @param scrapeFile
     */
    void updateScrapedBill(BillScrapeFile scrapeFile);

    /**
     * Gets bill scrape files pending processing.
     *
     * @param limitOffset {@link LimitOffset}
     * @return {@link List<LimitOffset>}
     */
    PaginatedList<BillScrapeFile> getPendingScrapeBills(LimitOffset limitOffset);

    /**
     * Stages reasonably un-stale archived scrape files of the given session for processing.
     *
     * Only stages files that were scraped after the latest update to their bill.
     * @param sessionYear {@link SessionYear}
     * @return int - number of files staged
     */
    int stageArchivedScrapeFiles(SessionYear sessionYear);

    /**
     * Gets the bill at the head of the scrape queue
     * @return BaseBillId
     * @throws EmptyResultDataAccessException if the scrape queue is empty
     */
    BaseBillId getScrapeQueueHead() throws EmptyResultDataAccessException;

    /**
     * Gets all bills in the scrape queue ordered by priority and added time
     * @param limitOffset LimitOffset
     * @param order SortOrder - results are ordered first by priority and then by added time with the opposite order
     *              e.g. SortOrder.DESC will return results with descending priority and ascending added time
     */
    PaginatedList<BillScrapeQueueEntry> getScrapeQueue(LimitOffset limitOffset, SortOrder order);

    /**
     * Adds a bill to the scrape queue
     */
    void addBillToScrapeQueue(BaseBillId baseBillId, int priority);

    /**
     * Overload of {@link #addBillToScrapeQueue(BaseBillId, int)}
     * uses the priority value of a {@link ScrapeQueuePriority}
     *
     * @param baseBillId {@link BaseBillId}
     * @param priority {@link ScrapeQueuePriority}
     */
    default void addBillToScrapeQueue(BaseBillId baseBillId, ScrapeQueuePriority priority) {
        addBillToScrapeQueue(baseBillId, Objects.requireNonNull(priority).getPriority());
    }

    /**
     * Removes all instances of a bill from the scrape queue
     */
    void deleteBillFromScrapeQueue(BaseBillId id);

}
