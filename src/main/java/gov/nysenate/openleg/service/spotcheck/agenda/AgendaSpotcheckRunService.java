package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaSpotcheckRunService extends BaseAgendaSpotcheckRunService {

    private static final Logger logger = LoggerFactory.getLogger(AgendaSpotcheckRunService.class);

    @Autowired
    private AgendaCheckReportService reportService;

    @Override
    protected SpotCheckReport<CommitteeAgendaAddendumId> generateReport() throws ReferenceDataNotFoundEx {
        logger.info("attempting to run an agenda spotcheck report");
        // Find unchecked references from any time
        SpotCheckReport<CommitteeAgendaAddendumId> report =
                reportService.generateReport(DateUtils.LONG_AGO.atStartOfDay(), DateUtils.THE_FUTURE.atStartOfDay());
        logger.info("saving agenda reports..");
        reportService.saveReport(report);
        return report;
    }
}
