package gov.nysenate.openleg.service.spotcheck.agenda;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.agenda.reference.AgendaAlertProcessor;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckRunService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public abstract class BaseAgendaSpotcheckRunService extends BaseSpotcheckRunService<CommitteeAgendaAddendumId> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAgendaSpotcheckRunService.class);

    @Autowired
    private AgendaAlertCheckMailService alertCheckMailService;

    @Autowired
    private CommAgendaAlertCheckMailService commAgendaAlertCheckMailService;

    @Autowired
    private AgendaAlertProcessor agendaAlertProcessor;

    /** --- Implemented Methods --- */

    @Override
    public List<SpotCheckReport<CommitteeAgendaAddendumId>> doGenerateReports() {
        try {
            return Collections.singletonList(generateReport());
        } catch (ReferenceDataNotFoundEx ex) {
            logger.info("No reports generated: {}", ex.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    protected int doCollate() throws Exception {
        int alertsDownloaded = alertCheckMailService.checkMail() + commAgendaAlertCheckMailService.checkMail();
        agendaAlertProcessor.processAgendaAlerts();
        return alertsDownloaded;
    }

    @Override
    public String getCollateType() {
        return "agenda alert";
    }

    /** --- Internal Methods --- */

    protected SpotCheckReport<CommitteeAgendaAddendumId> generateReport() throws ReferenceDataNotFoundEx {
        logger.info("attempting to run an agenda spotcheck report");
        // Find unchecked references from any time
        Range<LocalDateTime> checkRange = getCheckRange();
        SpotCheckReport<CommitteeAgendaAddendumId> report =
                getReportService().generateReport(DateUtils.startOfDateTimeRange(checkRange),
                                                  DateUtils.endOfDateTimeRange(checkRange));
        logger.info("saving agenda reports..");
        getReportService().saveReport(report);
        return report;
    }

    protected abstract SpotCheckReportService<CommitteeAgendaAddendumId> getReportService();

    protected Range<LocalDateTime> getCheckRange() {
        return DateUtils.ALL_DATE_TIMES;
    }
}
