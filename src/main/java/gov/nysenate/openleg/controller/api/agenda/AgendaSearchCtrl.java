package gov.nysenate.openleg.controller.api.agenda;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.agenda.AgendaCommFlatView;
import gov.nysenate.openleg.client.view.agenda.CommAgendaIdView;
import gov.nysenate.openleg.client.view.agenda.CommAgendaSummaryView;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.agenda.search.AgendaSearchService;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/agendas/", method = RequestMethod.GET)
public class AgendaSearchCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AgendaSearchCtrl.class);

    @Autowired private AgendaDataService agendaData;
    @Autowired private AgendaSearchService agendaSearch;
    @Autowired private BillDataService billData;

    /**
     * Agenda Search API
     * -----------------
     *
     * Search agendas across all year: (GET) /api/3/agendas/search
     * @see #searchAgendas(int, String, String, boolean, WebRequest)
     * for query parameter details.
     */
    @RequestMapping(value = "/search")
    public BaseResponse searchAgendas(@RequestParam(required = true) String term,
                                      @RequestParam(defaultValue = "") String sort,
                                      @RequestParam(defaultValue = "false") boolean full,
                                      WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<CommitteeAgendaId> results = agendaSearch.searchCommitteeAgendas(term, sort, limOff);
        return getAgendaSearchResponse(full, limOff, results);
    }

    /**
     * Agenda Search API
     * -----------------
     *
     * Search agendas for a given year: (GET) /api/3/agendas/{year}/search
     * Request Parameters:              term - The lucene query string
     *                                  sort - The lucene sort string
     *                                  full - Receive full agenda committee views
     *                                  limit - Limit the number of results
     *                                  offset - Start the results from offset
     */
    @RequestMapping(value = "/{year:[\\d]{4}}/search")
    public BaseResponse searchAgendas(@PathVariable int year,
                                      @RequestParam(required = true) String term,
                                      @RequestParam(defaultValue = "") String sort,
                                      @RequestParam(defaultValue = "false") boolean full,
                                      WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<CommitteeAgendaId> results = agendaSearch.searchCommitteeAgendas(term, year, sort, limOff);
        return getAgendaSearchResponse(full, limOff, results);
    }

    private BaseResponse getAgendaSearchResponse(boolean full, LimitOffset limOff, SearchResults<CommitteeAgendaId> results) {
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r ->
                    new SearchResultView((full)
                        ? new AgendaCommFlatView(agendaData.getAgenda(r.getResult().getAgendaId()),
                                                 r.getResult().getCommitteeId(), billData)
                        : new CommAgendaSummaryView(r.getResult(), agendaData.getAgenda(r.getResult().getAgendaId())),
                    r.getRank()))
                .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }
}
