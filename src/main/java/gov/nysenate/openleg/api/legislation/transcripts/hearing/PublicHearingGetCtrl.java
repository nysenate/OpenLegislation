package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingIdView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingInfoView;
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
     * Retrieve all public hearings: (GET) /api/3/hearings/
     */
    @RequestMapping(value = "")
    public BaseResponse getHearingsByYear(@RequestParam(defaultValue = "date:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean summary,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          WebRequest webRequest) throws SearchException {
        return getHearings(null, sort, summary, full, webRequest);
    }

    /**
     * Public Hearing Listing API.
     * --------------------------
     * <p>
     * Retrieve public hearings for a year: (GET) /api/3/hearings/{year}
     * <p>
     * Request Parameters : sort - Lucene syntax for sorting by any field of a public hearing response.
     * full - If true, the full public hearing view is returned. Otherwise just its id.
     * limit - Limit the number of results
     * offset - Start results from an offset.
     * <p>
     * Expected Output: List of PublicHearingIdView or PublicHearingView.
     */
    @RequestMapping(value = "/{strYear:\\d{4}}")
    public BaseResponse getHearingsByYear(@PathVariable String strYear,
                                          @RequestParam(defaultValue = "date:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean summary,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          WebRequest webRequest) throws SearchException {
        return getHearings(Integer.parseInt(strYear), sort, summary, full, webRequest);
    }

    private BaseResponse getHearings(Integer year, String sort, boolean summary, boolean full, WebRequest webRequest)
            throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<PublicHearingId> results = hearingSearch.searchPublicHearings(year, sort, limOff);
        return getListResponse(summary, full, limOff, results);
    }

    /**
     * Single Public Hearing Retrieval API.
     * Retrieve a singe public hearing by its id or filename.
     * (GET) /api/3/hearings/{id OR filename}
     * Request Parameters: None
     * Expected Output: PublicHearingView
     */
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
     * (GET) /api/3/hearings/{id OR filename}.pdf
     * Request Parameters: None.
     * Expected Output: PDF response.
     */

    @RequestMapping(value = "/{identifier}.pdf")
    public ResponseEntity<byte[]> getHearingPdf(@PathVariable String identifier) throws IOException {
        PublicHearing hearing;
        try {
            hearing = hearingData.getPublicHearing(new PublicHearingId(Integer.parseInt(identifier)));
        } catch (NumberFormatException ex) {
            hearing = hearingData.getPublicHearing(identifier);
        }
        return new PublicHearingPdfView(hearing).writeData();
    }

    /**
     * Returns an error response if a requested public hearing was not found
     *
     * @param ex PublicHearingNotFoundEx
     * @return ErrorResponse
     */
    @ExceptionHandler(PublicHearingNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handlePubHearingNotFoundEx(PublicHearingNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.PUBLIC_HEARING_NOT_FOUND, new PublicHearingIdView(ex.getId(), ex.getFilename()));
    }

    private ListViewResponse<ViewObject> getListResponse(boolean summary, boolean full, LimitOffset limOff,
                                                         SearchResults<PublicHearingId> results) {
        return ListViewResponse.of(results.getResults().stream().map(r ->
                        getHearingViewObject(r.getResult(), summary, full))
                .toList(), results.getTotalResults(), limOff);
    }

    private ViewObject getHearingViewObject(PublicHearingId id, boolean summary, boolean full) {
        if (full)
            return new PublicHearingView(hearingData.getPublicHearing(id));
        if (summary)
            return new PublicHearingInfoView(hearingData.getPublicHearing(id));
        return new PublicHearingIdView(id, hearingData.getFilename(id));
    }
}
