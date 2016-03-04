package gov.nysenate.openleg.controller.api.transcript;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.transcript.TranscriptIdView;
import gov.nysenate.openleg.client.view.transcript.TranscriptInfoView;
import gov.nysenate.openleg.client.view.transcript.TranscriptView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.service.transcript.data.TranscriptDataService;
import gov.nysenate.openleg.service.transcript.search.TranscriptSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Transcript Search API.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/transcripts", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class TranscriptSearchCtrl extends BaseCtrl
{
    @Autowired private TranscriptDataService transcriptData;
    @Autowired private TranscriptSearchService transcriptSearch;

    /**
     * Transcript Search API
     * ---------------------
     *
     * Search all transcripts:  (GET) /api/3/transcripts/search
     * Request Parameters:  term - The lucene query string.
     *                      sort - The lucene sort string (blank by default)
     *                      summary - If true, the transcript info is returned. (true by default)
     *                      full - Set to true to retrieve full transcript responses (false by default)
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
        SearchResults<TranscriptId> results = transcriptSearch.searchTranscripts(term, sort, limOff);
        return getSearchResponse(summary, full, limOff, results);
    }

    /**
     * Transcript Search by Year
     * -------------------------
     *
     *  Search all transcripts in a given year: (GET) /api/3/transcripts/{year}/search
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
        SearchResults<TranscriptId> results = transcriptSearch.searchTranscripts(term, year, sort, limOff);
        return getSearchResponse(summary, full, limOff, results);
    }

    private BaseResponse getSearchResponse(boolean summary, boolean full, LimitOffset limOff, SearchResults<TranscriptId> results) {
        return ListViewResponse.of(results.getResults().stream().map(r -> new SearchResultView(
                (full) ? new TranscriptView(transcriptData.getTranscript(r.getResult()))
                        : (summary) ? new TranscriptInfoView(transcriptData.getTranscript(r.getResult()))
                        : new TranscriptIdView(r.getResult()), r.getRank(), r.getHighlights()))
                .collect(toList()), results.getTotalResults(), limOff);
    }
}
