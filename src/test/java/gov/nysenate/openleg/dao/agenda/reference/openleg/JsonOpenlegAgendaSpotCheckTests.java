package gov.nysenate.openleg.dao.agenda.reference.openleg;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.spotcheck.openleg.OpenlegAgendaReportService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class JsonOpenlegAgendaSpotCheckTests extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegAgendaSpotCheckTests.class);

    @Autowired
    OpenlegAgendaReportService openlegAgendaReportService;

    @Test
    public void testGetAgendaView() throws Exception {
        SpotCheckReport<CommitteeAgendaAddendumId> spotCheckReport = openlegAgendaReportService.generateReport(LocalDateTime.parse("2017-01-01T00:00:00"),null);
        openlegAgendaReportService.saveReport(spotCheckReport);
    }

}
