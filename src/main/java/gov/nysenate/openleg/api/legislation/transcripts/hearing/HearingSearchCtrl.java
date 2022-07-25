package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingIdView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingInfoView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.hearing.HearingSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Hearing Search API.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class HearingSearchCtrl extends BaseCtrl {
    private final HearingDataService hearingData;
    private final HearingSearchService hearingSearch;

    @Autowired
    public HearingSearchCtrl(HearingDataService hearingData, HearingSearchService hearingSearch) {
        this.hearingData = hearingData;
        this.hearingSearch = hearingSearch;
    }

    /**
     * Hearing Search API.
     * ---------------
     * <p>
     * Search all hearings:  (GET) /api/3/hearings/search
     * Request Parameters:  term - The lucene query string.
     * sort - The lucene sort string (blank by default)
     * full - Set to true to retrieve full hearing responses (false by default)
     * limit - Limit the number of resultList (default 25)
     * offset - Start resultList from offset
     */
    @RequestMapping(value = "/search")
    public BaseResponse globalSearch(@RequestParam String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "true") boolean summary,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<HearingId> results = hearingSearch.searchHearings(term, sort, limOff);
        return getSearchResponse(full, summary, limOff, results);
    }

    /**
     * Hearing Search by Year.
     * ---------------
     * <p>
     * Search all Hearings in a given year: (GET) /api/3/hearings/{year}/search
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
        SearchResults<HearingId> results = hearingSearch.searchHearings(term, year, sort, limOff);
        return getSearchResponse(full, summary, limOff, results);
    }

    private BaseResponse getSearchResponse(boolean full, boolean summary, LimitOffset limOff, SearchResults<HearingId> results) {
        return ListViewResponse.of(results.resultList().stream().map(r -> new SearchResultView(
                        getHearingViewObject(r.result(), full, summary), r.rank(), r.highlights()))
                .toList(), results.totalResults(), limOff);
    }

    private ViewObject getHearingViewObject(HearingId id, boolean full, boolean summary) {
        if (full)
            return new HearingView(hearingData.getHearing(id));
        if (summary)
            return new HearingInfoView(hearingData.getHearing(id));
        return new HearingIdView(id, hearingData.getFilename(id));
    }
}
