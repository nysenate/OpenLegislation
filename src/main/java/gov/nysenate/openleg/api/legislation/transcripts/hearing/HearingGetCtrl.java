package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.CollectionFullPolicy;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingIdView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingPdfView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.transcripts.hearing.HearingSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.List;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class HearingGetCtrl extends BaseCtrl {
    private final HearingDataService hearingData;
    private final HearingSearchService hearingSearch;
    private final CollectionFullPolicy collectionFullPolicy;

    @Autowired
    public HearingGetCtrl(HearingDataService hearingData, HearingSearchService hearingSearch,
                          CollectionFullPolicy collectionFullPolicy) {
        this.hearingData = hearingData;
        this.hearingSearch = hearingSearch;
        this.collectionFullPolicy = collectionFullPolicy;
    }

    /**
     * Retrieve all hearings: (GET) /api/3/hearings/
     *
     * @see #getHearingsByYear see getHearingsByYear for request params.
     */
    @RequestMapping(value = "")
    public BaseResponse getHearings(@RequestParam(defaultValue = "date:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean summary,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          WebRequest webRequest) throws SearchException {
        return getHearings(null, sort, summary, full, webRequest);
    }

    /**
     * Hearing Listing API.
     * --------------------------
     * <p>
     * Retrieve hearings for a year: (GET) /api/3/hearings/{year}
     * <p>
     * Request Parameters : sort - Lucene syntax for sorting by any field of a hearing response.
     * summary - If true, the hearing info is returned. Otherwise, just its id.
     * full - If true, the full hearing view is returned (overrides summary).
     *        Retired: rejected when api.collection_full.mode is enforce, even with summary.
     * limit - Limit the number of results
     * offset - Start results from an offset.
     * <p>
     * Expected Output: List of HearingIdView, HearingInfoView or HearingView.
     */
    @RequestMapping(value = "/{strYear:\\d{4}}")
    public BaseResponse getHearingsByYear(@PathVariable String strYear,
                                          @RequestParam(defaultValue = "date:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean summary,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          WebRequest webRequest) throws SearchException {
        return getHearings(Integer.parseInt(strYear), sort, summary, full, webRequest);
    }

    private BaseResponse getHearings(Integer year, String sort, boolean summary, boolean full,
                                     WebRequest webRequest) throws SearchException {
        collectionFullPolicy.check(CollectionFullPolicy.Option.HEARING_FULL, full, webRequest);
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<HearingId> results = hearingSearch.searchHearings(year, sort, limOff);
        List<ViewObject> views = results.resultList().stream()
                .map(searchResult -> HearingViews.of(hearingData, searchResult.result(), summary, full)).toList();
        return ListViewResponse.of(views, results.totalResults(), limOff);
    }

    /**
     * Single Hearing Retrieval API.
     * Retrieve a singe hearing by its id or filename.
     * (GET) /api/3/hearings/{id OR filename}
     * Request Parameters: None
     * Expected Output: HearingView
     */
    @RequestMapping(value = "/{id:\\d{1,3}}")
    public BaseResponse getHearingById(@PathVariable String id) {
        return new ViewObjectResponse<>(new HearingView(
                hearingData.getHearing(new HearingId(Integer.parseInt(id)))),
                "Data for hearing with id #" + id);
    }

    @RequestMapping(value = "/{filename:.*\\D.*}")
    public BaseResponse getHearingByFilename(@PathVariable String filename) {
        return new ViewObjectResponse<>(new HearingView(
                hearingData.getHearing(filename)),
                "Data for hearing with filename " + filename);
    }

    /**
     * Single Hearing PDF retrieval API.
     * Retrieve a single hearing text pdf:
     * (GET) /api/3/hearings/{id OR filename}.pdf
     * Request Parameters: None.
     * Expected Output: PDF response.
     */

    @RequestMapping(value = "/{identifier}.pdf")
    public ResponseEntity<byte[]> getHearingPdf(@PathVariable String identifier) throws IOException {
        Hearing hearing;
        try {
            hearing = hearingData.getHearing(new HearingId(Integer.parseInt(identifier)));
        } catch (NumberFormatException ex) {
            hearing = hearingData.getHearing(identifier);
        }
        return new HearingPdfView(hearing).writeData();
    }

    /**
     * Returns an error response if a requested hearing was not found
     * @param ex HearingNotFoundEx
     * @return ErrorResponse
     */
    @ExceptionHandler(HearingNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handlePubHearingNotFoundEx(HearingNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.HEARING_NOT_FOUND, new HearingIdView(ex.getId(), ex.getFilename()));
    }
}
