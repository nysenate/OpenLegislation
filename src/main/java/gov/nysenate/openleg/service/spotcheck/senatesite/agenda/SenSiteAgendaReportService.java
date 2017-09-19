package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.spotcheck.CommitteeAgendaAddendumIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpId;
import gov.nysenate.openleg.model.spotcheck.senatesite.agenda.SenateSiteAgenda;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
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
public class SenSiteAgendaReportService extends BaseSpotCheckReportService<CommitteeAgendaAddendumId>{
    private static final Logger logger = LoggerFactory.getLogger(SenSiteAgendaReportService.class);

    @Autowired
    private SenateSiteDao senateSiteDao;
    @Autowired
    private SenateSiteAgendaCheckService senateSiteAgendaCheckService;
    @Autowired
    private AgendaJsonParser agendaJsonParser;
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
        try {
            SenateSiteDumpId dumpId = agendaDump.getDumpId();
            SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_AGENDA,
                    dumpId.getDumpTime(), LocalDateTime.now());
            SpotCheckReport<CommitteeAgendaAddendumId> report = new SpotCheckReport<>(reportId);
            report.setNotes(agendaDump.getDumpId().getNotes());

            logger.info("Loading agendas for year {} ...", dumpId.getYear());

            Map<CommitteeAgendaAddendumId, AgendaVoteCommittee> voteData = getVotesForYear(dumpId.getYear());

            logger.info("Extracting agenda references ...");

            List<SenateSiteAgenda> senateSiteAgendas = agendaJsonParser.parseAgendas(agendaDump);

            logger.info("Checking agendas ... ");

            checkAgendaVotes(report, voteData, senateSiteAgendas);

            return report;
        }
        finally {
            logger.info("archiving agenda dump...");
            senateSiteDao.setProcessed(agendaDump);
        }
    }

    /**
     * Get the most recent agenda dump from the senate site dao
     */
    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_AGENDA).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full senate site agenda dumps"));
    }

    /**
     * Load all {@link AgendaVoteCommittee} for the given year from openleg
     */
    private Map<CommitteeAgendaAddendumId, AgendaVoteCommittee> getVotesForYear(int year) {
        return agendaDataService.getAgendaIds(year, SortOrder.NONE).stream()
                .map(agendaDataService::getAgenda)
                .map(Agenda::getVotes)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Checks a set of reference agenda votes against agenda votes loaded from openleg,
     * saving the result in the given spotcheck report.
     *
     * @param report {@link SpotCheckReport<CommitteeAgendaAddendumId>}
     * @param dataVotes {@link Map<CommitteeAgendaAddendumId, AgendaVoteCommittee>}
     * @param refAgendas {@link List<SenateSiteAgenda>}
     */
    private void checkAgendaVotes(SpotCheckReport<CommitteeAgendaAddendumId> report,
                                  Map<CommitteeAgendaAddendumId, AgendaVoteCommittee> dataVotes,
                                  List<SenateSiteAgenda> refAgendas) {

        // Tracks the data agendas that have not been checked from the given ref agendas
        // The remaining ids can be considered to be missing from the reference set
        Set<CommitteeAgendaAddendumId> uncheckedDataVotes = new HashSet<>(dataVotes.keySet());

        // Check the reference agendas against the data agendas
        for (SenateSiteAgenda refAgenda : refAgendas) {
            CommitteeAgendaAddendumId id = refAgenda.getcommitteeAgendaAddendumId();

            if (dataVotes.containsKey(id)) {
                AgendaVoteCommittee dataAgenda = dataVotes.get(id);
                SpotCheckObservation<CommitteeAgendaAddendumId> obs =
                        senateSiteAgendaCheckService.check(dataAgenda, refAgenda);
                report.addObservation(obs);
                uncheckedDataVotes.remove(id);
            } else {
                report.addObservedDataMissingObs(id);
            }
        }

        // Save the remaining data votes as ref data missing mismatches
        uncheckedDataVotes.forEach(report::addRefMissingObs);
    }

}
