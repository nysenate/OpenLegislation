package gov.nysenate.openleg.api.legislation.transcripts.session;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptInfoView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.DuplicateTranscriptEx;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.session.TranscriptSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDateTime;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Transcript retrieval APIs.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/transcripts", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class TranscriptGetCtrl extends BaseCtrl {
    private static final int TRANSCRIPT_DEFAULT_LIMIT = 25;
    private final TranscriptDataService transcriptData;
    private final TranscriptSearchService transcriptSearch;

    @Autowired
    public TranscriptGetCtrl(TranscriptDataService transcriptData, TranscriptSearchService transcriptSearch) {
        this.transcriptData = transcriptData;
        this.transcriptSearch = transcriptSearch;
    }

    /**
     * Transcript Listing API
     * ----------------------
     * Retrieve all transcripts: (GET) /api/3/transcripts/
     * Request Parameters : sort - Lucene syntax for sorting by any field of a transcript response.
     *                      summary - If true, the transcript info is returned.
     *                      full - If true, the full transcript view is returned. Otherwise, just its filename.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     * Expected Output: List of TranscriptView or TranscriptIdView.
     */
    @RequestMapping(value = "")
    public BaseResponse getAllTranscripts(@RequestParam(defaultValue = "dateTime:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean summary,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          @RequestParam(defaultValue = "true") boolean sessionOnly,
                                          WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, TRANSCRIPT_DEFAULT_LIMIT);
        SearchResults<TranscriptId> results = transcriptSearch.searchTranscripts(sort, limOff, sessionOnly);
        return getTranscriptResponse(summary, full, limOff, results);
    }

    /**
     * Transcript Listing API
     * ----------------------
     * Retrieve transcripts for a year: (GET) /api/3/transcripts/{year}
     * Request Parameters : sort - Lucene syntax for sorting by any field of a transcript response.
     *                      summary - If true, the transcript info is returned.
     *                      full - If true, the full transcript view is returned. Otherwise, just its filename.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     * Expected Output: List of TranscriptIdView or TranscriptView
     */
    @RequestMapping("/{year:\\d{4}}")
    public BaseResponse getTranscriptsByYear(@PathVariable int year,
                                             @RequestParam(defaultValue = "dateTime:desc") String sort,
                                             @RequestParam(defaultValue = "false") boolean summary,
                                             @RequestParam(defaultValue = "false") boolean full,
                                             @RequestParam(defaultValue = "true") boolean sessionOnly,
                                             WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, TRANSCRIPT_DEFAULT_LIMIT);
        SearchResults<TranscriptId> results = transcriptSearch.searchTranscripts(year, sort, limOff, sessionOnly);
        return getTranscriptResponse(summary, full, limOff, results);
    }


    /**
     * Single Transcript Retrieval API
     * -------------------------------
     * Retrieve a single transcripts by its filename (GET) /api/3/transcripts/{dateTime}
     * <p>Request Parameters: None.</p>
     * Expected Output: TranscriptView
     */
    @RequestMapping("/{dateTime:.*}")
    public BaseResponse getTranscript(@PathVariable String dateTime) {
        LocalDateTime localDateTime = parseISODateTime(dateTime, "dateTime");
        return new ViewObjectResponse<>(
                new TranscriptView(transcriptData.getTranscriptByDateTime(localDateTime)),
                "Data for transcript " + dateTime);
    }

    /**
     * Single Transcript Retrieval API
     * -------------------------------
     * Retrieve a single transcripts by its filename (GET) /api/3/transcripts/{dateTime}/{sessionType}
     * <p>Request Parameters: None.</p>
     * Expected Output: TranscriptView
     */
    @RequestMapping("/{dateTime}/{sessionType}")
    public BaseResponse getTranscript(@PathVariable String dateTime, @PathVariable String sessionType) {
        var id = TranscriptId.from(parseISODateTime(dateTime, "dateTime"), sessionType);
        return new ViewObjectResponse<>(new TranscriptView(transcriptData.getTranscript(id)),
                "Data for transcript " + dateTime);
    }

    /**
     * Single Transcript PDF retrieval API
     * -----------------------------------
     * Retrieve a single transcript text pdf: (GET) /api/3/transcripts/{dateTime}.pdf
     * Request Parameters: None.
     * Expected Output: PDF response.
     */

    @RequestMapping("/{dateTime}.pdf")
    public ResponseEntity<byte[]> getTranscriptPdf(@PathVariable String dateTime)
            throws IOException {
        LocalDateTime localDateTime = parseISODateTime(dateTime, "dateTime");
        Transcript transcript = transcriptData.getTranscriptByDateTime(localDateTime);
        return new TranscriptPdfView(transcript).writeData();
    }

    @RequestMapping("/{dateTime}/{sessionType}.pdf")
    public ResponseEntity<byte[]> getTranscriptPdf(@PathVariable String dateTime, @PathVariable String sessionType)
            throws IOException {
        LocalDateTime localDateTime = parseISODateTime(dateTime, "dateTime");
        var id = TranscriptId.from(localDateTime, sessionType);
        Transcript transcript = transcriptData.getTranscript(id);
        return new TranscriptPdfView(transcript).writeData();
    }

    /** --- Internal --- */

    private BaseResponse getTranscriptResponse(boolean summary, boolean full, LimitOffset limOff, SearchResults<TranscriptId> results) {
        return ListViewResponse.of(results.resultList().stream().map(r ->
            (full) ? new TranscriptView(transcriptData.getTranscript(r.result()))
                    : (summary) ? new TranscriptInfoView(transcriptData.getTranscript(r.result()))
                    : new TranscriptIdView(r.result()))
            .toList(), results.totalResults(), limOff);
    }

    @ExceptionHandler(TranscriptNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleTranscriptNotFoundEx(TranscriptNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.TRANSCRIPT_NOT_FOUND, new TranscriptIdView(ex.getTranscriptId()));
    }

    @ExceptionHandler(DuplicateTranscriptEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleTranscriptNotFoundEx(DuplicateTranscriptEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.DUPLICATE_TRANSCRIPT, ex.getDateTime());
    }
}
