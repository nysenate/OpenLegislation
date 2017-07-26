package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.agenda.data.AgendaUpdatesDao;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.spotcheck.CommitteeAgendaAddendumIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpId;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by PKS on 4/28/16.
 */
@Service
public class AgendaReportServices extends BaseSpotCheckReportService<CommitteeAgendaAddendumId>{
    private static final Logger logger = LoggerFactory.getLogger(AgendaReportServices.class);

    @Autowired
    private SenateSiteDao senateSiteDao;
    @Autowired
    private AgendaCheckServices agendaCheckServices;
    @Autowired
    private AgendaJsonParser agendaJsonParser;
    @Autowired
    private AgendaUpdatesDao agendaUpdatesDao;
    @Autowired
    private AgendaDataService agendaDataService;
    @Autowired
    private CommitteeAgendaAddendumIdSpotCheckReportDao committeeAgendaAddendumIdSpotCheckReportDao;

    @Override
    protected SpotCheckReportDao<CommitteeAgendaAddendumId> getReportDao() {
        return committeeAgendaAddendumIdSpotCheckReportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.SENATE_SITE_AGENDA;
    }

    @Override
    public SpotCheckReport<CommitteeAgendaAddendumId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        SenateSiteDump agendaDump = getMostRecentDump();
        SenateSiteDumpId dumpId = agendaDump.getDumpId();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_AGENDA,
                dumpId.getDumpTime(), LocalDateTime.now());
        SpotCheckReport<CommitteeAgendaAddendumId> report = new SpotCheckReport<>(reportId);
        report.setNotes(agendaDump.getDumpId().getNotes());
        // todo in depth refactoring needed to fix this
//        try {
//
//            logger.info("getting agenda updates");
//
//            // Get reference agendas using the agenda dump update interval
//            Set<AgendaId> updatedAgendaIds = getAgendaUpdatesDuring(agendaDump);
//            logger.info("got {} updated agenda ids", updatedAgendaIds.size());
//            Map<AgendaId, Agenda> updatedAgendas = new LinkedHashMap<>();
//            logger.info("retrieving agendas");
//            for (AgendaId agendaId : updatedAgendaIds) {
//                try {
//                    updatedAgendas.put(agendaId, agendaDataService.getAgenda(agendaId));
//                } catch (AgendaNotFoundEx ex) {
//                    SpotCheckObservation<CommitteeAgendaAddendumId> observation = new SpotCheckObservation<>(reportId.getReferenceId(),
//                            new CommitteeAgendaAddendumId(agendaId, null, Version.DEFAULT));
//                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", agendaId));
//                    report.addObservation(observation);
//                }
//            }
//            logger.info("got {} agendas", updatedAgendas.size());
//            logger.info("retrieving agenda dump");
//            // Extract senate site agendas from the dump
//            Multimap<CommitteeAgendaAddendumId, SenateSiteAgenda> dumpedAgendas = ArrayListMultimap.create();
//            agendaJsonParser.parseAgendas(agendaDump).forEach(b -> dumpedAgendas.put(b.getcommitteeAgendaAddendumId(), b));
//            logger.info("parsed {} dumped agendas", dumpedAgendas.size());
//
//            prunePostDumpAgendas(agendaDump, report, dumpedAgendas, updatedAgendas);
//
//            logger.info("comparing agendas present");
//            // Add observations for any missing calendars that should have been in the dump
//            Tuple<List<SpotCheckObservation<CommitteeAgendaAddendumId>>,List<CommitteeAgendaAddendumId>> obs =
//                    getRefDataMissingObs(dumpedAgendas.values(), updatedAgendas.values(),
//                    reportId.getReferenceId());
//            report.addObservations(obs.v1());
//
//            logger.info("checking agendas");
//            // Check each dumped senate site calendar
//            dumpedAgendas.values().stream()
//                    .filter(senateSiteAgenda -> updatedAgendas.containsKey(senateSiteAgenda.getAgendaId())
//                            && !obs.v2().contains(senateSiteAgenda.getcommitteeAgendaAddendumId()))
//                    .map(senateSiteAgenda-> agendaCheckServices.check(updatedAgendas.get(senateSiteAgenda.getAgendaId()),
//                            senateSiteAgenda))
//                    .forEach(report::addObservation);
//
//            logger.info("done: {} mismatches", report.getOpenMismatchCount(false));
//        } finally {
//            logger.info("archiving agenda dump...");
//            senateSiteDao.setProcessed(agendaDump);
//        }
        return report;
    }

    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_AGENDA).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full senate site agenda dumps"));
    }

    private Tuple<List<SpotCheckObservation<CommitteeAgendaAddendumId>>, List<CommitteeAgendaAddendumId>> getRefDataMissingObs(Collection<SenateSiteAgenda> senSiteAgendas,
                                                                                                                   Collection<Agenda> openlegAgendas,
                                                                                                                   SpotCheckReferenceId refId) {
        Set<CommitteeAgendaAddendumId> senSiteAgendaIds = senSiteAgendas.stream()
                .map(SenateSiteAgenda::getcommitteeAgendaAddendumId)
                .collect(Collectors.toSet());

        Set<CommitteeAgendaAddendumId> openlegAgendaIds = openlegAgendas.stream()
                .map(Agenda::getCommitteeAgendaAddendumIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<CommitteeAgendaAddendumId> toRemove = new ArrayList<>();

        List<SpotCheckObservation<CommitteeAgendaAddendumId>> refData = Sets.difference(openlegAgendaIds, senSiteAgendaIds).stream()
                .map(agendaId -> {
                    SpotCheckObservation<CommitteeAgendaAddendumId> observation =
                            new SpotCheckObservation<>(refId, agendaId);
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.REFERENCE_DATA_MISSING, "", ""));
                    return observation;
                }).collect(Collectors.toList());

        List<SpotCheckObservation<CommitteeAgendaAddendumId>> obsData = Sets.difference(senSiteAgendaIds,openlegAgendaIds).stream()
                .map(agendaId -> {
                    SpotCheckObservation<CommitteeAgendaAddendumId> observation =
                            new SpotCheckObservation<>(refId, agendaId);
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", ""));
                    toRemove.add(agendaId);
                    return observation;
                }).collect(Collectors.toList());

        List<SpotCheckObservation<CommitteeAgendaAddendumId>> allDataObs = new ArrayList<>();
        allDataObs.addAll(refData);
        allDataObs.addAll(obsData);
        return Tuple.tuple(allDataObs,toRemove);
    }
}
