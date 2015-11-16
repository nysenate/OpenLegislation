package gov.nysenate.openleg.controller.api.transcript;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.transcript.TranscriptIdView;
import gov.nysenate.openleg.client.view.transcript.TranscriptInfoView;
import gov.nysenate.openleg.client.view.transcript.TranscriptPdfView;
import gov.nysenate.openleg.client.view.transcript.TranscriptView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptNotFoundEx;
import gov.nysenate.openleg.service.transcript.data.TranscriptDataService;
import gov.nysenate.openleg.service.transcript.search.TranscriptSearchService;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Transcript retrieval APIs.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/transcripts", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class TranscriptGetCtrl extends BaseCtrl
{
    @Autowired
    private TranscriptDataService transcriptData;

    @Autowired
    private TranscriptSearchService transcriptSearch;

    /**
     * Transcript Listing API
     * ----------------------
     *
     * Retrieve all transcripts: (GET) /api/3/transcripts/
     * Request Parameters : sort - Lucene syntax for sorting by any field of a transcript response.
     *                      summary - If true, the transcript info is returned.
     *                      full - If true, the full transcript view is returned. Otherwise just its filename.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     *
     * Expected Output: List of TranscriptView or TranscriptIdView.
     */
    @RequestMapping(value = "")
    public BaseResponse getAllTranscripts(@RequestParam(defaultValue = "dateTime:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean summary,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<TranscriptId> results = transcriptSearch.searchTranscripts(sort, limOff);
        return getTranscriptResponse(summary, full, limOff, results);
    }

    /**
     * Transcript Listing API
     * ----------------------
     *
     * Retrieve transcripts for a year: (GET) /api/3/transcripts/{year}
     * Request Parameters : sort - Lucene syntax for sorting by any field of a transcript response.
     *                      summary - If true, the transcript info is returned.
     *                      full - If true, the full transcript view is returned. Otherwise just its filename.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     *
     * Expected Output: List of TranscriptIdView or TranscriptView
     */
    @RequestMapping("/{year:[\\d]{4}}")
    public BaseResponse getTranscriptsByYear(@PathVariable int year,
                                             @RequestParam(defaultValue = "dateTime:desc") String sort,
                                             @RequestParam(defaultValue = "false") boolean summary,
                                             @RequestParam(defaultValue = "false") boolean full,
                                             WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<TranscriptId> results = transcriptSearch.searchTranscripts(year, sort, limOff);
        return getTranscriptResponse(summary, full, limOff, results);
    }

    /**
     * Single Transcript Retrieval API
     * -------------------------------
     *
     * Retrieve a single transcripts by its filename (GET) /api/3/transcripts/{filename}
     *
     * <p>Request Parameters: None.</p>
     *
     * Expected Output: TranscriptView
     */
    @RequestMapping("/{filename:.*}")
    public BaseResponse getTranscript(@PathVariable String filename) {
        return new ViewObjectResponse<>(
            new TranscriptView(transcriptData.getTranscript(new TranscriptId(filename))),
                "Data for transcript " + filename);
    }

    /**
     * Single Transcript PDF retrieval API
     * -----------------------------------
     *
     * Retrieve a single transcript text pdf: (GET) /api/3/transcripts/{filename}.pdf
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping("/{filename}.pdf")
    public void getTranscriptPdf(@PathVariable String filename,
                                 HttpServletResponse response) throws IOException, COSVisitorException {
        TranscriptId transcriptId = new TranscriptId(filename);
        Transcript transcript = transcriptData.getTranscript(transcriptId);
        new TranscriptPdfView(transcript, response.getOutputStream());
        response.setContentType("application/pdf");
    }

    /** --- Internal --- */

    private BaseResponse getTranscriptResponse(boolean summary, boolean full, LimitOffset limOff, SearchResults<TranscriptId> results) {
        return ListViewResponse.of(results.getResults().stream().map(r ->
            (full) ? new TranscriptView(transcriptData.getTranscript(r.getResult()))
                    : (summary) ? new TranscriptInfoView(transcriptData.getTranscript(r.getResult()))
                    : new TranscriptIdView(r.getResult()))
            .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    @ExceptionHandler(TranscriptNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleTranscriptNotFoundEx(TranscriptNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.TRANSCRIPT_NOT_FOUND, new TranscriptIdView(ex.getTranscriptId()));
    }
}
