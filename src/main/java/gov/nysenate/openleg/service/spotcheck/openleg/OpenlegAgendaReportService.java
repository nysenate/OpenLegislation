package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.client.view.agenda.AgendaCommAddendumView;
import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.dao.agenda.reference.openleg.OpenlegAgendaDao;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.OBSERVE_DATA_MISSING;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.REFERENCE_DATA_MISSING;

@Service("openlegAgendaReport")
public class OpenlegAgendaReportService extends BaseSpotCheckReportService<CommitteeAgendaAddendumId> {
    private static final Logger logger = LoggerFactory.getLogger(OpenlegAgendaReportService.class);

    @Value("api.secret")
    private  String apiSecret;

    @Autowired
    private SpotCheckReportDao<CommitteeAgendaAddendumId> reportDao;

    @Autowired
    private OpenlegAgendaDao openlegAgendaDao;

    @Autowired
    private AgendaDataService agendaDataService;

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
        List<AgendaView> referenceAgendaViews = openlegAgendaDao.getOpenlegAgendaView(String.valueOf(start.getYear()), apiSecret);
        if (referenceAgendaViews.isEmpty()) {
            throw new ReferenceDataNotFoundEx("The collection of reference agendas with the given session year " + SessionYear.of(start.getYear()) + " is empty");
        }

        //Create Maps of content and source AgendaCommAddendumView
        Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> contentAddendumViewMap = new HashMap<>();
        Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> referenceAddendumViewMap = new HashMap<>();

        //Get content data, transform it into an AgendaView then to a AgendaCommAddendumView and store in contentAddendumViewMap
        for (AgendaId contentAgendaId : agendaDataService.getAgendaIds(start.getYear(), SortOrder.NONE) ) {
          AgendaView contentAgendaView = new AgendaView(agendaDataService.getAgenda(contentAgendaId) ,billDataService );
          putAgendaCommAddendumViewIntoMap(contentAgendaView , contentAddendumViewMap);
        }

        //Retrieve AgendaCommAddendumView from reference agenda views and store it in referenceAddendumViewMap
        for(AgendaView refAgendaView: referenceAgendaViews) {
            putAgendaCommAddendumViewIntoMap(refAgendaView , referenceAddendumViewMap);
        }

        //Put content Id's a set
        Set<CommitteeAgendaAddendumId> remainingContentIds = new HashSet<>(contentAddendumViewMap.keySet());

        //Find symmetric difference between content and reference id's
        referenceAddendumViewMap.forEach((id, referenceAddendum) -> {
            if (contentAddendumViewMap.containsKey(id)) {
                //Both Calendars have the same Addendum item
                AgendaCommAddendumView contentAddendum = contentAddendumViewMap.get(id);
                SpotCheckObservation<CommitteeAgendaAddendumId> observation = checkService.check(contentAddendum, referenceAddendum);
                report.addObservation(observation);
            } else {
                //add data missing
                SpotCheckObservation<CommitteeAgendaAddendumId> sourceMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), id);
                sourceMissingObs.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, id, "Missing Data from Openleg XML, ID:" + id.toString()));
                report.addObservation(sourceMissingObs);
            }
            remainingContentIds.remove(id);
        });

        remainingContentIds.forEach(id -> {
            // add ref missing
            SpotCheckObservation<CommitteeAgendaAddendumId> refMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), id);
            refMissingObs.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, id, "Missing Data from Openleg Ref, ID:" + id.toString()));
            report.addObservation(refMissingObs);
        });

        logger.info("Found total number of " + report.getOpenMismatchCount(false) + " mismatches");

        return report;
    }

    private void putAgendaCommAddendumViewIntoMap(AgendaView agendaView,Map<CommitteeAgendaAddendumId, AgendaCommAddendumView> addendumViewMap) {
        agendaView.getCommitteeAgendas()
                .getItems()
                .stream()
                .forEach(
                        agendaCommView -> agendaCommView.getAddenda()
                                .getItems()
                                .stream()
                                .forEach(
                                        agendaCommAddendumView ->
                                                addendumViewMap.put(agendaCommAddendumView.getCommitteeAgendaAddendumId() , agendaCommAddendumView)));
    }
}
