package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakCheckService;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DaybreakSpotCheckReportDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotCheckReportDaoTests.class);

    @Autowired
    DaybreakCheckService daybreakCheckService;

    @Autowired
    BaseBillIdSpotCheckReportDao reportDao;

    @Test
    public void testGetReports() throws Exception {
        List<SpotCheckReportSummary> reportSummaries =
                reportDao.getReportSummaries(SpotCheckRefType.LBDC_SCRAPED_BILL,
                        DateUtils.LONG_AGO.atStartOfDay(), DateUtils.THE_FUTURE.atStartOfDay(), SortOrder.DESC);
        Optional<LocalDateTime> reportDateTime = reportSummaries.stream()
                .filter(report -> report.getMismatchStatuses().containsKey(SpotCheckMismatchStatus.NEW))
                .findAny().map(SpotCheckReportSummary::getReportId).map(SpotCheckReportId::getReportDateTime);

        if (reportDateTime.isPresent()) {
            SpotCheckReport<BaseBillId> report = reportDao.getReport(
                    new SpotCheckReportId(SpotCheckRefType.LBDC_SCRAPED_BILL, reportDateTime.get()));
            logger.info("{}", OutputUtils.toJson(report.getReportId()));
        }
    }

    @Test
    public void setIgnoreStatusTest() {
        //Shouldn't do anything
        reportDao.setMismatchIgnoreStatus(-1, SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        // Should set ignore status
        reportDao.setMismatchIgnoreStatus(5235, SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        // Should delete ignore status
        reportDao.setMismatchIgnoreStatus(5235, null);
    }

    @Test
    public void getOpenObsTest() {
        //Map<BaseBillId, SpotCheckObservation<BaseBillId>> obs = reportDao.getOpenMismatches(SpotCheckRefType.LBDC_SCRAPED_BILL);
        logger.info("hi");
    }

    @Test
    public void testReportIds() throws Exception {
        logger.info("{}", reportDao.getReportSummaries(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.of(2014, 1, 1, 0, 0, 0), LocalDateTime.now(), SortOrder.DESC));
    }

    @Test
    public void testEndToEnd() throws Exception {


    }
}
