package gov.nysenate.openleg.spotchecks.sensite.agenda;

import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.agenda.dao.AgendaDataService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReport;
import gov.nysenate.openleg.spotchecks.sensite.BaseSenateSiteReportService;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by PKS on 4/28/16.
 */
@Service
public class SenSiteAgendaReportService extends BaseSenateSiteReportService<CommitteeAgendaAddendumId> {
    private static final Logger logger = LoggerFactory.getLogger(SenSiteAgendaReportService.class);

    private final SenateSiteAgendaCheckService senateSiteAgendaCheckService;
    private final AgendaJsonParser agendaJsonParser;
    private final AgendaDataService agendaDataService;

    public SenSiteAgendaReportService(SenateSiteAgendaCheckService senateSiteAgendaCheckService,
                                      AgendaJsonParser agendaJsonParser,
                                      AgendaDataService agendaDataService) {
        this.senateSiteAgendaCheckService = senateSiteAgendaCheckService;
        this.agendaJsonParser = agendaJsonParser;
        this.agendaDataService = agendaDataService;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.SENATE_SITE_AGENDA;
    }

    @Override
    protected void checkDump(SenateSiteDump dump, SpotCheckReport<CommitteeAgendaAddendumId> report) {
        logger.info("Loading agendas for year {} ...", dump.getDumpId().year());
        Map<AgendaId, Agenda> openlegAgendas = getOpenlegAgendas(dump.getDumpId().year());

        logger.info("Extracting agenda references ...");
        List<SenateSiteAgenda> senateSiteAgendas = agendaJsonParser.parseAgendas(dump);

        logger.info("Checking agendas ... ");
        checkAgendas(report, openlegAgendas, senateSiteAgendas);
    }

    /**
     * Get all stored agendas for the given year
     */
    private Map<AgendaId, Agenda> getOpenlegAgendas(int year) {
        List<AgendaId> agendaIds = agendaDataService.getAgendaIds(year, SortOrder.ASC);
        return agendaIds.stream()
                .map(agendaDataService::getAgenda)
                .collect(Collectors.toMap(Agenda::getId, Function.identity()));
    }

    /**
     * Checks a set of reference agenda votes against agenda votes loaded from openleg,
     * saving the result in the given spotcheck report.
     *  @param report {@link SpotCheckReport<CommitteeAgendaAddendumId>}
     * @param openlegAgendas {@link Map<CommitteeAgendaAddendumId, AgendaVoteCommittee>}
     * @param refAgendas {@link List<SenateSiteAgenda>}
     */
    private void checkAgendas(SpotCheckReport<CommitteeAgendaAddendumId> report,
                              Map<AgendaId, Agenda> openlegAgendas,
                              List<SenateSiteAgenda> refAgendas) {

        // Tracks the data agendas that have not been checked from the given ref agendas
        // The remaining ids can be considered to be missing from the reference set
        Set<CommitteeAgendaAddendumId> uncheckedOpenlegMeetings = openlegAgendas.values().stream()
                .map(Agenda::getCommitteeAgendaAddendumIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashSet::new));

        // Check the reference agendas against the data agendas
        for (SenateSiteAgenda refAgenda : refAgendas) {
            CommitteeAgendaAddendumId meetingId = refAgenda.getcommitteeAgendaAddendumId();
            AgendaId agendaId = meetingId.getAgendaId();
            uncheckedOpenlegMeetings.remove(meetingId);

            if (openlegAgendas.containsKey(agendaId)) {
                Agenda openlegAgenda = openlegAgendas.get(agendaId);
                SpotCheckObservation<CommitteeAgendaAddendumId> obs =
                        senateSiteAgendaCheckService.check(openlegAgenda, refAgenda);
                report.addObservation(obs);
            } else {
                report.addObservedDataMissingObs(meetingId);
            }
        }

        // Save the remaining data meetings as ref data missing mismatches
        uncheckedOpenlegMeetings.forEach(report::addRefMissingObs);
    }

}
