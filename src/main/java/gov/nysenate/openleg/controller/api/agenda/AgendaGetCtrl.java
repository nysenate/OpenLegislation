package gov.nysenate.openleg.controller.api.agenda;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.agenda.AgendaCommFlatView;
import gov.nysenate.openleg.client.view.agenda.AgendaIdView;
import gov.nysenate.openleg.client.view.agenda.AgendaSummaryView;
import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.agenda.search.AgendaSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/agendas", method = RequestMethod.GET)
public class AgendaGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaGetCtrl.class);

    @Autowired private AgendaDataService agendaDataService;
    @Autowired private AgendaSearchService agenda;

    /**
     * Agenda List Retrieval API
     *
     * Get agenda list by year: (GET) /api/3/agendas/{year}
     * Returns a list of agenda ids in ascending order that occur in the given 'year'.
     */
    @RequestMapping(value = "/{year}")
    public BaseResponse getAgendas(@PathVariable int year) {
        List<AgendaId> agendaIds = agendaDataService.getAgendaIds(year, SortOrder.ASC);
        return ListViewResponse.of(
            agendaIds.stream()
                    .map(aid -> new AgendaSummaryView(agendaDataService.getAgenda(aid)))
                    .collect(Collectors.toList()), agendaIds.size(), LimitOffset.ALL);
    }

    /**
     * Agenda Retrieval API
     *
     * Retrieve a specific agenda in full: (GET) /api/3/agendas/{year}/{agendaNo}
     * where 'year' is the calendar year of the agenda and agendaNo is the number that identifies
     * the agenda. This response will contain data for committee agendas.
     */
    @RequestMapping(value = "/{year:[\\d]{4}}/{agendaNo}")
    public BaseResponse getAgenda(@PathVariable int year, @PathVariable int agendaNo) {
        Agenda agenda = agendaDataService.getAgenda(new AgendaId(agendaNo, year));
        return new ViewObjectResponse<>(new AgendaView(agenda));
    }

    /**
     * Committee Agenda Retrieval API
     *
     * Retrieve a specific committee within an agenda: (GET) /api/3/agendas/{year}/{agendaNo}/{committeeName}
     * where year and agendaNo are the same as {@link #getAgenda(int, int)} and 'committeeName' refers to the
     * name of the senate committee.
     */
    @RequestMapping(value = "/{year:[\\d]{4}}/{agendaNo}/{commName}")
    public BaseResponse getAgenda(@PathVariable int year, @PathVariable int agendaNo, @PathVariable String commName) {
        Agenda agenda = agendaDataService.getAgenda(new AgendaId(agendaNo, year));
        CommitteeId committeeId = new CommitteeId(Chamber.SENATE, commName);
        if (agenda.hasCommittee(committeeId)) {
            return new ViewObjectResponse<>(new AgendaCommFlatView(agenda, committeeId));
        }
        else {
            return new ViewObjectErrorResponse(
                ErrorCode.INVALID_ARGUMENTS, commName + " is not contained within agenda " + agendaNo + " (" + year + ")");
        }
    }

    /** --- Exception Handlers --- */

    @ExceptionHandler(AgendaNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ViewObjectErrorResponse agendaNotFoundHandler(AgendaNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.AGENDA_NOT_FOUND, new AgendaIdView(ex.getAgendaId()));
    }
}
