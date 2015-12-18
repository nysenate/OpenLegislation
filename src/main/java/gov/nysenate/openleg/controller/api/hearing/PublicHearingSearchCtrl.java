package gov.nysenate.openleg.controller.api.hearing;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.hearing.PublicHearingIdView;
import gov.nysenate.openleg.client.view.hearing.PublicHearingInfoView;
import gov.nysenate.openleg.client.view.hearing.PublicHearingView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.hearing.data.PublicHearingDataService;
import gov.nysenate.openleg.service.hearing.search.PublicHearingSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Public Hearing Search API.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class PublicHearingSearchCtrl extends BaseCtrl
{
    @Autowired private PublicHearingDataService hearingData;
    @Autowired private PublicHearingSearchService hearingSearch;

    /**
     * Public Hearing Search API.
     * ---------------
     *
     * Search all public hearings:  (GET) /api/3/hearings/search
     * Request Parameters:  term - The lucene query string.
     *                      sort - The lucene sort string (blank by default)
     *                      full - Set to true to retrieve full public hearing responses (false by default)
     *                      limit - Limit the number of results (default 25)
     *                      offset - Start results from offset
     */
    @RequestMapping(value = "/search")
    public BaseResponse globalSearch(@RequestParam(required = true) String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "true") boolean summary,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<PublicHearingId> results = hearingSearch.searchPublicHearings(term, sort, limOff);
        return getSearchResponse(full, summary, limOff, results);
    }

    /**
     * Public Hearing Search by Year.
     *  ---------------
     *
     *  Search all Public Hearings in a given year: (GET) /api/3/hearings/{year}/search
     *  @see #globalSearch see globalSearch for request params.
     */
    @RequestMapping(value = "/{year:[\\d]{4}}/search")
    public BaseResponse yearSearch(@PathVariable int year,
                                   @RequestParam(required = true) String term,
                                   @RequestParam(defaultValue = "") String sort,
                                   @RequestParam(defaultValue = "true") boolean summary,
                                   @RequestParam(defaultValue = "false") boolean full,
                                   WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<PublicHearingId> results = hearingSearch.searchPublicHearings(term, year, sort, limOff);
        return getSearchResponse(full, summary, limOff, results);
    }

    private BaseResponse getSearchResponse(boolean full, boolean summary, LimitOffset limOff, SearchResults<PublicHearingId> results) {
        return ListViewResponse.of(results.getResults().stream().map(r -> new SearchResultView(
                (full) ? new PublicHearingView(hearingData.getPublicHearing(r.getResult()))
                        : (summary) ? new PublicHearingInfoView(hearingData.getPublicHearing(r.getResult()))
                           : new PublicHearingIdView(r.getResult()), r.getRank(), r.getHighlights()))
                .collect(toList()), results.getTotalResults(), limOff);
    }
}
