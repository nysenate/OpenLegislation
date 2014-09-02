package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DaybreakSpotCheckReportServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotCheckReportServiceTests.class);

    @Autowired
    DaybreakCheckReportService daybreakReportService;

    @Test
    public void testGenerateReport() throws Exception {
        SpotCheckReport<BaseBillId> report =
            daybreakReportService.generateReport(DateUtils.longAgo(), LocalDateTime.now());
        daybreakReportService.saveReport(report);
    }

    @Test
    public void testGetReport() throws Exception {
        SpotCheckReportId reportId = daybreakReportService.getReportIds(DateUtils.longAgo(), LocalDateTime.now(),
                                                                        SortOrder.DESC, LimitOffset.ALL).get(0);
        SpotCheckReport<BaseBillId> report = daybreakReportService.getReport(reportId);
        logger.info("{}", report.getMismatchStatusCounts());
        logger.info("{}", report.getMismatchTypeCounts());
//        report.getObservations().forEach((k,v) -> logger.info("{}", v.getPriorMismatches()));
    }
}
