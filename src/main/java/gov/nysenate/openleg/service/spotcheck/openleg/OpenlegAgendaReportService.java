package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.dao.agenda.reference.openleg.OpenlegAgendaDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OpenlegAgendaReportService extends BaseSpotCheckReportService<CommitteeAgendaAddendumId> {
    private static final Logger logger = LoggerFactory.getLogger(OpenlegBillReportService.class);

    @Value("api.secret")
    private  String apiSecret;

    @Autowired
    private SpotCheckReportDao<CommitteeAgendaAddendumId> reportDao;

    @Autowired
    private OpenlegAgendaDao openlegAgendaDao;

    @Autowired
    private BillDataService billDataService;

    @Autowired
    OpenlegBillCheckService checkService;

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
        return null;
    }
}
