package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.agenda.AgendaCommAddendumView;
import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.dao.agenda.reference.openleg.OpenlegAgendaDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class OpenlegAgendaReportService extends BaseSpotCheckReportService<CommitteeAgendaAddendumId> {
    private static final Logger logger = LoggerFactory.getLogger(OpenlegAgendaReportService.class);

    @Value("api.secret")
    private  String apiSecret;

    @Autowired
    private SpotCheckReportDao<CommitteeAgendaAddendumId> reportDao;

    @Autowired
    private OpenlegAgendaDao openlegAgendaDao;

    @Autowired
    private BillDataService billDataService;

    @Autowired
    OpenlegAgendaCheckService checkService;

    @Override
    protected SpotCheckReportDao<CommitteeAgendaAddendumId> getReportDao() {
        return reportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.OPENLEG_AGENDA;
    }

    @Override
    public SpotCheckReport<CommitteeAgendaAddendumId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        logger.info("Start generating new agenda spot check report between Openleg Ref and XML Branch");

        //Create New spotcheck report
        SpotCheckReport<CommitteeAgendaAddendumId> report = new SpotCheckReport<>();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.OPENLEG_AGENDA, LocalDateTime.now(), LocalDateTime.now());
        report.setReportId(reportId);

        logger.info("Loading Agendas from Openleg Ref");
        logger.info("The current session year is " + SessionYear.of(start.getYear()));

        //Retrieve Openleg Ref Agenda data
        List<AgendaView> referenceCalendarViews = openlegAgendaDao.getOpenlegAgendaView(String.valueOf(start.getYear()), apiSecret);
        if (referenceCalendarViews.isEmpty()) {
            throw new ReferenceDataNotFoundEx("The collection of reference agendas with the given session year " + SessionYear.of(start.getYear()) + " is empty");
        }

        //Create Maps of content and source AgendaCommAddendumView
        Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> referenceAddendumViewMap = new HashMap<>();
        Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> contentAddendumViewMap = new HashMap<>();



        return report;
    }
}
