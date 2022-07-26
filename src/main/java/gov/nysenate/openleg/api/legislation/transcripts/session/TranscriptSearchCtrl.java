package gov.nysenate.openleg.api.legislation.transcripts.session;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptInfoView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.session.TranscriptSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Transcript Search API.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/transcripts", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class TranscriptSearchCtrl extends BaseCtrl {
    private static final int TRANSCRIPT_DEFAULT_LIMIT = 25;
    private final TranscriptDataService transcriptData;
    private final TranscriptSearchService transcriptSearch;

    @Autowired
    public TranscriptSearchCtrl(TranscriptDataService transcriptData, TranscriptSearchService transcriptSearch) {
        this.transcriptData = transcriptData;
        this.transcriptSearch = transcriptSearch;
    }

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
    public BaseResponse globalSearch(@RequestParam String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "true") boolean summary,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, TRANSCRIPT_DEFAULT_LIMIT);
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
    @RequestMapping(value = "/{year:\\d{4}}/search")
    public BaseResponse yearSearch(@PathVariable int year,
                                   @RequestParam String term,
                                   @RequestParam(defaultValue = "") String sort,
                                   @RequestParam(defaultValue = "true") boolean summary,
                                   @RequestParam(defaultValue = "false") boolean full,
                                   WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, TRANSCRIPT_DEFAULT_LIMIT);
        SearchResults<TranscriptId> results = transcriptSearch.searchTranscripts(term, year, sort, limOff);
        return getSearchResponse(summary, full, limOff, results);
    }

    private BaseResponse getSearchResponse(boolean summary, boolean full, LimitOffset limOff, SearchResults<TranscriptId> results) {
        return ListViewResponse.of(results.resultList().stream().map(r -> new SearchResultView(
                (full) ? new TranscriptView(transcriptData.getTranscript(r.result()))
                        : (summary) ? new TranscriptInfoView(transcriptData.getTranscript(r.result()))
                        : new TranscriptIdView(r.result()), r.rank(), r.highlights()))
                .toList(), results.totalResults(), limOff);
    }
}
