package gov.nysenate.openleg.dao.bill.text;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillScrapeQueueEntry;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Created by kyle on 2/19/15.
 */
public interface BillTextReferenceDao {

    /**
     * @return A list of all incoming scraped bill files
     */
    Collection<File> getIncomingScrapedBills() throws IOException;

    /**
     * Moves a scraped bill file into the archive directory
     * @param scrapedBill File
     * @throws IOException if the file could not be archived
     */
    void archiveScrapedBill(File scrapedBill) throws IOException;

    /**
     * Gets a list of all unchecked bill text references
     */
    List<BillTextReference> getUncheckedBillTextReferences();

    /**
     * Gets the most recent bill text reference for the given bill within the given date time range
     */
    BillTextReference getMostRecentBillTextReference(BaseBillId id, LocalDateTime start, LocalDateTime end);

    /**
     * Gets the most recently scraped bill text reference within the given date time range
     */
    BillTextReference getMostRecentBillTextReference(LocalDateTime start, LocalDateTime end);

    /**
     * Gets a bill text reference for the given base bill id that was scraped at the given date time
     */
    BillTextReference getBillTextReference(BaseBillId id, LocalDateTime refDateTime);

    /**
     * Gets all bill text references for the given billid
     */
    List<BillTextReference> getBillTextReference(BaseBillId id);

    /**
     * Inserts a new bill text reference
     */
    void insertBillTextReference(BillTextReference ref);

    /** Sets all references for the given bill id as checked */
    void setChecked(BaseBillId billId);

    /** Deletes a bill text reference */
    void deleteBillTextReference(BillTextReference ref);

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
     * Removes all instances of a bill from the scrape queue
     */
    void deleteBillFromScrapeQueue(BaseBillId id);

}
