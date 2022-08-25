package gov.nysenate.openleg.spotchecks.scraping.lrs.bill.ctrl;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.bill.view.BaseBillIdView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.spotcheck.view.BillScrapeQueueEntryView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.BillScrapeQueueEntry;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.BillScrapeReferenceDao;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.ScrapeQueuePriority;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/scraping/billqueue")
public class BillScrapeQueueCtrl extends BaseCtrl {
    private final BillScrapeReferenceDao btrDao;

    @Autowired
    public BillScrapeQueueCtrl(BillScrapeReferenceDao btrDao) {
        this.btrDao = btrDao;
    }

    /**
     * Get Scrape Queue API
     *
     * Get a list of bills that are in the scrape queue
     * Usage: (GET) /api/3/admin/scraping/billqueue
     *
     * Request Parameters: limit - Limit the number of entries in the response
     *                     offset - offset the start of the response
     *                     order - determines order of response (default DESC)
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getBillScrapeQueue(WebRequest request) {
        LimitOffset limitOffset = getLimitOffset(request, 0);
        SortOrder order = getSortOrder(request, SortOrder.DESC);
        PaginatedList<BillScrapeQueueEntry> results = btrDao.getScrapeQueue(limitOffset, order);
        return ListViewResponse.of(
                results.results().stream()
                        .map(BillScrapeQueueEntryView::new)
                        .toList(),
                results.total(), results.limOff()
        );
    }

    /**
     * Push to Scrape Queue API
     *
     * Add a bill to the scrape queue
     * Usage: (PUT) /api/3/admin/scraping/billqueue/{sessionYear}/{printNo}
     *
     * Request Parameters: priority - an integer that determines the priority of the enqueued bill
     *                                  @see ScrapeQueuePriority#MANUAL_ENTRY for default value
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/{sessionYear:\\d+}/{printNo}", method = RequestMethod.PUT)
    public BaseResponse addBillToScrapeQueue(@PathVariable int sessionYear,
                                             @PathVariable String printNo,
                                             @RequestParam(required = false) Integer priority) {
        BaseBillId baseBillId = getBaseBillId(printNo, sessionYear, "printNo");
        if (priority == null) {
            priority = ScrapeQueuePriority.MANUAL_ENTRY.getPriority();
        }
        btrDao.addBillToScrapeQueue(baseBillId, priority);
        return new ViewObjectResponse<>(new BaseBillIdView(baseBillId), "added bill to scrape queue");
    }

    /**
     * Delete from Scrape Queue API
     *
     * Remove a bill from the scrape queue
     * Usage: (DELETE) /api/3/admin/scraping/billqueue/{sessionYear}/{printNo}
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/{sessionYear}/{printNo}", method = RequestMethod.DELETE)
    public BaseResponse removeBillFromScrapeQueue(@PathVariable int sessionYear,
                                                  @PathVariable String printNo) {
        BaseBillId baseBillId = getBaseBillId(printNo, sessionYear, "printNo");
        btrDao.deleteBillFromScrapeQueue(baseBillId);
        return new ViewObjectResponse<>(new BaseBillIdView(baseBillId), "removed bill from scrape queue");
    }
}
