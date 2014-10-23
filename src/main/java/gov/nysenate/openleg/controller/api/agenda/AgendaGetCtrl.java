package gov.nysenate.openleg.controller.api.agenda;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.agenda.AgendaSummaryView;
import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/agendas", method = RequestMethod.GET)
public class AgendaGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaGetCtrl.class);

    @Autowired
    private AgendaDataService agendaDataService;

    @RequestMapping(value = "/{year}")
    public BaseResponse getAgendas(@PathVariable int year) {
        List<AgendaId> agendaIds = agendaDataService.getAgendaIds(year, SortOrder.ASC);
        return ListViewResponse.of(
            agendaIds.parallelStream()
                    .map(aid -> new AgendaSummaryView(agendaDataService.getAgenda(aid)))
                    .collect(Collectors.toList()), agendaIds.size(), LimitOffset.ALL);
    }

    @RequestMapping(value = "/{year:[\\d]{4}}/{agendaNo}")
    public BaseResponse getAgenda(@PathVariable int year, @PathVariable int agendaNo) {
        Agenda agenda = agendaDataService.getAgenda(new AgendaId(agendaNo, year));
        return new ViewObjectResponse<>(new AgendaView(agenda));
    }
}
