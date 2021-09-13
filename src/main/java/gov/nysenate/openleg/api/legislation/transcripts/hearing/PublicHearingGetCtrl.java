package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingIdView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingPdfView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.hearing.PublicHearingSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class PublicHearingGetCtrl extends BaseCtrl {
    @Autowired
    private PublicHearingDataService hearingData;

    @Autowired
    private PublicHearingSearchService hearingSearch;

    /**
     * Public Hearing Listing API.
     * --------------------------
     *
     * Retrieve public hearings for a year: (GET) /api/3/hearings/{year}
     * Retrieve all public hearings: (GET) /api/3/hearings/
     * Request Parameters : sort - Lucene syntax for sorting by any field of a public hearing response.
     *                      full - If true, the full public hearing view is returned. Otherwise just its id.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     *
     * Expected Output: List of PublicHearingIdView or PublicHearingView.
     */
    @RequestMapping(value = "/{strYear:\\d{4}|^$}")
    public BaseResponse getHearingsByYear(@PathVariable String strYear,
                                          @RequestParam(defaultValue = "date:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          WebRequest webRequest)
                                          throws SearchException {
        // A null year will return all hearings.
        Integer year = strYear.isEmpty() ? null : Integer.parseInt(strYear);
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<PublicHearingId> results = hearingSearch.searchPublicHearings(year, sort, limOff);
        return ListViewResponse.of(results.getResults().stream().map(r ->
                        (full) ? new PublicHearingView(hearingData.getPublicHearing(r.getResult()))
                                : new PublicHearingIdView(r.getResult()))
                .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    /**
     * Single Public Hearing Retrieval API.
     * Retrieve a singe public hearing by its id or filename.
     * (GET) /api/3/hearings/{id OR filename}
     * Request Parameters: None
     * Expected Output: PublicHearingView
     */
    // TODO: what about when we get to 4 digits?
    @RequestMapping(value = "/{id:\\d{1,3}}")
    public BaseResponse getHearingById(@PathVariable String id) {
        return new ViewObjectResponse<>(new PublicHearingView(
                hearingData.getPublicHearing(new PublicHearingId(Integer.parseInt(id)))),
        "Data for public hearing with id #" + id);
    }

    @RequestMapping(value = "/{filename:.*\\D.*}")
    public BaseResponse getHearingByFilename(@PathVariable String filename) {
        return new ViewObjectResponse<>(new PublicHearingView(
                hearingData.getPublicHearing(filename)),
                "Data for public hearing with filename " + filename);
    }

    /**
     * Single Public Hearing PDF retrieval API.
     * Retrieve a single public hearing text pdf:
     * (GET) /api/3/hearings/{id OR filename}.pdf or
     * Request Parameters: None.
     * Expected Output: PDF response.
     */
    @RequestMapping(value = "/{id:\\d{1,3}}.pdf")
    public ResponseEntity<byte[]> getHearingPdfFromId(@PathVariable String id)
            throws IOException {
        PublicHearing hearing = hearingData.getPublicHearing(new PublicHearingId(Integer.parseInt(id)));
        return new PublicHearingPdfView(hearing).writeData();
    }

    @RequestMapping(value = "/{filename::.*\\D.*}.pdf")
    public ResponseEntity<byte[]> getHearingPdfFromFilename(@PathVariable String filename)
            throws IOException {
        PublicHearing hearing = hearingData.getPublicHearing(filename);
        return new PublicHearingPdfView(hearing).writeData();
    }

    /**
     * Returns an error response if a requested public hearing was not found
     * @param ex PublicHearingNotFoundEx
     * @return ErrorResponse
     */
    @ExceptionHandler(PublicHearingNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handlePubHearingNotFoundEx(PublicHearingNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.PUBLIC_HEARING_NOT_FOUND, new PublicHearingIdView(ex.getPublicHearingId()));
    }
}
