package gov.nysenate.openleg.spotchecks.scraping.lrs.bill.ctrl;

import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.BillScrapeReferenceDao;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/spotcheck/scrapefile", produces = APPLICATION_JSON_VALUE)
public class BillScrapeFileCtrl extends BaseCtrl {

    private BillScrapeReferenceDao refDao;

    @Autowired
    public BillScrapeFileCtrl(BillScrapeReferenceDao refDao) {
        this.refDao = refDao;
    }

    @RequestMapping(value = "/stagearchived/{session:\\d+}", method = RequestMethod.POST)
    @RequiresPermissions("admin:spotcheck:stage-scrape-file")
    public SimpleResponse stageArchivedScrapeFiles(@PathVariable int session) {
        int staged = refDao.stageArchivedScrapeFiles(getSessionYearParam(session, "session"));
        return new SimpleResponse(true, "Staged " + staged + " scrape files.",
                "scrape-file-stage-success");
    }
}
