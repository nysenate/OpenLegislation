package gov.nysenate.openleg.service.spotcheck.agenda;

import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OldApiAgendaSpotcheckRunService extends BaseAgendaSpotcheckRunService {

    @Autowired OldApiAgendaCheckReportService reportService;

    @Override
    protected SpotCheckReportService<CommitteeAgendaAddendumId> getReportService() {
        return reportService;
    }
}
