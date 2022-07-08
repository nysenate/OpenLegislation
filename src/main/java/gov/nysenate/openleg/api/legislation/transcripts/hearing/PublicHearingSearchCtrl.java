package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingIdView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingInfoView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.hearing.PublicHearingSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Public Hearing Search API.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class PublicHearingSearchCtrl extends BaseCtrl {
    @Autowired
    private PublicHearingDataService hearingData;

    @Autowired
    private PublicHearingSearchService hearingSearch;

    /**
     * Public Hearing Search API.
     * ---------------
     * <p>
     * Search all public hearings:  (GET) /api/3/hearings/search
     * Request Parameters:  term - The lucene query string.
     * sort - The lucene sort string (blank by default)
     * full - Set to true to retrieve full public hearing responses (false by default)
     * limit - Limit the number of results (default 25)
     * offset - Start results from offset
     */
    @RequestMapping(value = "/search")
    public BaseResponse globalSearch(@RequestParam String term,
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
     * ---------------
     * <p>
     * Search all Public Hearings in a given year: (GET) /api/3/hearings/{year}/search
     *
     * @see #globalSearch see globalSearch for request params.
     */
    @RequestMapping(value = "/{year:\\d{4}}/search")
    public BaseResponse yearSearch(@PathVariable int year,
                                   @RequestParam String term,
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
                        getHearingViewObject(r.getResult(), full, summary), r.getRank(), r.getHighlights()))
                .toList(), results.getTotalResults(), limOff);
    }

    private ViewObject getHearingViewObject(PublicHearingId id, boolean full, boolean summary) {
        if (full)
            return new PublicHearingView(hearingData.getPublicHearing(id));
        if (summary)
            return new PublicHearingInfoView(hearingData.getPublicHearing(id));
        return new PublicHearingIdView(id, hearingData.getFilename(id));
    }
}
