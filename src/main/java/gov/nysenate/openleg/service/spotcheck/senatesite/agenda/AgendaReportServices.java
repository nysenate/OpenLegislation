package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import com.google.common.collect.*;
import gov.nysenate.openleg.dao.agenda.data.AgendaUpdatesDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.spotcheck.CommitteeAgendaAddendumIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpSessionId;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_AGENDA,
                DateUtils.endOfDateTimeRange(agendaDump.getDumpId().getRange()), LocalDateTime.now());
        SpotCheckReport<CommitteeAgendaAddendumId> report = new SpotCheckReport<>(reportId);
        report.setNotes(agendaDump.getDumpId().getNotes());
        try {

            logger.info("getting agenda updates");

            // Get reference agendas using the agenda dump update interval
            Set<AgendaId> updatedAgendaIds = getAgendaUpdatesDuring(agendaDump);
            logger.info("got {} updated agenda ids", updatedAgendaIds.size());
            Map<AgendaId, Agenda> updatedAgendas = new LinkedHashMap<>();
            logger.info("retrieving agendas");
            for (AgendaId agendaId : updatedAgendaIds) {
                try {
                    updatedAgendas.put(agendaId, agendaDataService.getAgenda(agendaId));
                } catch (AgendaNotFoundEx ex) {
                    SpotCheckObservation<CommitteeAgendaAddendumId> observation = new SpotCheckObservation<>(reportId.getReferenceId(),
                            new CommitteeAgendaAddendumId(agendaId, null, Version.DEFAULT));
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", agendaId));
                    report.addObservation(observation);
                }
            }
            logger.info("got {} agendas", updatedAgendas.size());
            logger.info("retrieving agenda dump");
            // Extract senate site agendas from the dump
            Multimap<CommitteeAgendaAddendumId, SenateSiteAgenda> dumpedAgendas = ArrayListMultimap.create();
            agendaJsonParser.parseAgendas(agendaDump).forEach(b -> dumpedAgendas.put(b.getcommitteeAgendaAddendumId(), b));
            logger.info("parsed {} dumped agendas", dumpedAgendas.size());

            prunePostDumpAgendas(agendaDump, report, dumpedAgendas, updatedAgendas);

            logger.info("comparing agendas present");
            // Add observations for any missing calendars that should have been in the dump
            Tuple<List<SpotCheckObservation<CommitteeAgendaAddendumId>>,List<CommitteeAgendaAddendumId>> obs =
                    getRefDataMissingObs(dumpedAgendas.values(), updatedAgendas.values(),
                    reportId.getReferenceId());
            report.addObservations(obs.v1());

            logger.info("checking agendas");
            // Check each dumped senate site calendar
            dumpedAgendas.values().stream()
                    .filter(senateSiteAgenda -> updatedAgendas.containsKey(senateSiteAgenda.getAgendaId())
                            && !obs.v2().contains(senateSiteAgenda.getcommitteeAgendaAddendumId()))
                    .map(senateSiteAgenda-> agendaCheckServices.check(updatedAgendas.get(senateSiteAgenda.getAgendaId()),
                            senateSiteAgenda))
                    .forEach(report::addObservation);

            logger.info("done: {} mismatches", report.getOpenMismatchCount(false));
        } finally {
            logger.info("archiving agenda dump...");
            senateSiteDao.setProcessed(agendaDump);
        }
        return report;
    }

    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_AGENDA).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full senate site agenda dumps"));
    }

    private Set<AgendaId> getAgendaUpdatesDuring(SenateSiteDump agendaDump) {
        if(agendaDump.getDumpId() instanceof SenateSiteDumpSessionId){
            Set<AgendaId> agendaIds = agendaDataService.getAgendaIds(((SenateSiteDumpSessionId) agendaDump.getDumpId()).getSession().getSessionStartYear(),SortOrder.NONE)
                    .stream()
                    .collect(Collectors.toCollection(TreeSet::new));
            Set<AgendaId> agendaIds1 = agendaDataService.getAgendaIds(((SenateSiteDumpSessionId) agendaDump.getDumpId()).getSession().getSessionEndYear(),SortOrder.NONE)
                    .stream()
                    .collect(Collectors.toCollection(TreeSet::new));
            agendaIds.addAll(agendaIds1);
            return agendaIds;
        }
        Range<LocalDateTime> dumpUpdateInterval = agendaDump.getDumpId().getRange();
        return agendaUpdatesDao.getUpdates(Range.greaterThan(DateUtils.startOfDateTimeRange(agendaDump.getDumpId().getRange())),
                UpdateType.PROCESSED_DATE,
                SortOrder.ASC, LimitOffset.ALL)
                .getResults().stream()
                .filter(token -> dumpUpdateInterval.contains(token.getProcessedDateTime()))
                .map(UpdateToken::getId)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private void prunePostDumpAgendas(SenateSiteDump agendaDump, SpotCheckReport report,
                                        Multimap<CommitteeAgendaAddendumId, SenateSiteAgenda> senSiteAgendas, Map<AgendaId, Agenda> openlegAgendas) {
        Range<LocalDateTime> agendaDumpRange = agendaDump.getDumpId().getRange();
        Range<LocalDateTime> postDumpRange =  Range.downTo(DateUtils.endOfDateTimeRange(agendaDumpRange),
                agendaDumpRange.upperBoundType() == BoundType.OPEN ? BoundType.CLOSED : BoundType.OPEN);
        PaginatedList<UpdateToken<AgendaId>> postDumpUpdates =
                agendaUpdatesDao.getUpdates(postDumpRange, UpdateType.PROCESSED_DATE, SortOrder.NONE, LimitOffset.ALL);
        Set<AgendaId> postDumpUpdatedAgendas = postDumpUpdates.stream()
                .map(UpdateToken::getId)
                .collect(Collectors.toSet());

        if (!postDumpUpdatedAgendas.isEmpty()) {
            // Iterate over calendars updated after the update interval, removing them from the references and
            //  collecting them in a list to add to the report notes
            String notes = postDumpUpdatedAgendas.stream()
                    .peek(senSiteAgendas::removeAll)
                    .peek(openlegAgendas::remove)
                    .reduce("Ignored Agendas:", (str, agendaId) -> str + " " + agendaId, (a, b) -> a + " " + b);
            report.setNotes(notes);
        }
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
