package gov.nysenate.openleg.spotchecks.openleg.agenda;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaCommAddendumView;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaView;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReport;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReportId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckRefType.OPENLEG_AGENDA;

@Service("openlegAgendaReport")
public class OpenlegAgendaReportService implements SpotCheckReportService<CommitteeAgendaAddendumId> {

    private static final Logger logger = LoggerFactory.getLogger(OpenlegAgendaReportService.class);

    private final OpenlegAgendaDao openlegAgendaDao;
    private final AgendaDataService agendaDataService;
    private final BillDataService billDataService;
    private final OpenlegAgendaCheckService checkService;

    @Autowired
    public OpenlegAgendaReportService(OpenlegAgendaDao openlegAgendaDao,
                                      AgendaDataService agendaDataService,
                                      BillDataService billDataService,
                                      OpenlegAgendaCheckService checkService) {
        this.openlegAgendaDao = openlegAgendaDao;
        this.agendaDataService = agendaDataService;
        this.billDataService = billDataService;
        this.checkService = checkService;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return OPENLEG_AGENDA;
    }

    @Override
    public SpotCheckReport<CommitteeAgendaAddendumId> generateReport(LocalDateTime start, LocalDateTime end) {
        // Create New spotcheck report
        SpotCheckReportId reportId = new SpotCheckReportId(OPENLEG_AGENDA, LocalDateTime.now(), LocalDateTime.now());
        SpotCheckReport<CommitteeAgendaAddendumId> report = new SpotCheckReport<>(reportId);

        int year = start.getYear();

        // Get Local agenda data
        List<AgendaView> contentAgendaViews =  agendaDataService.getAgendaIds(year, SortOrder.NONE).stream()
                .map(agendaDataService::getAgenda)
                .map(agenda -> new AgendaView(agenda, billDataService))
                .collect(Collectors.toList());
        // Retrieve Openleg Ref Agenda data
        List<AgendaView> referenceAgendaViews = openlegAgendaDao.getAgendaViews(year);

        // Create Maps of content and source AgendaCommAddendumView
        Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> contentAddenda = getAddendumMap(contentAgendaViews);
        Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> referenceAddenda = getAddendumMap(referenceAgendaViews);

        Set<CommitteeAgendaAddendumId> allIds = Sets.union(contentAddenda.keySet(), referenceAddenda.keySet());

        // Check addenda
        for (CommitteeAgendaAddendumId id : allIds) {
            if (!contentAddenda.containsKey(id)) {
                report.addObservedDataMissingObs(id);
            } else if (!referenceAddenda.containsKey(id)) {
                report.addRefMissingObs(id);
            } else {
                AgendaCommAddendumView contentAddendum = contentAddenda.get(id);
                AgendaCommAddendumView refAddendum = referenceAddenda.get(id);
                SpotCheckObservation<CommitteeAgendaAddendumId> obs = checkService.check(contentAddendum, refAddendum);
                report.addObservation(obs);
            }
        }

        return report;
    }

    private Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> getAddendumMap(Collection<AgendaView> agendaViews) {
        return agendaViews.stream()
                .flatMap(agendaView -> agendaView.getCommitteeAgendas().getItems().stream())
                .flatMap(agendaCommView -> agendaCommView.addenda().getItems().stream())
                .collect(Collectors.toMap(AgendaCommAddendumView::getCommitteeAgendaAddendumId, Function.identity()));
    }
}
