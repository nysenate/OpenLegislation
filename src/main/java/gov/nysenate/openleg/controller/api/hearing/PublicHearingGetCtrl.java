package gov.nysenate.openleg.controller.api.hearing;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.hearing.PublicHearingIdView;
import gov.nysenate.openleg.client.view.hearing.PublicHearingPdfView;
import gov.nysenate.openleg.client.view.hearing.PublicHearingView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.hearing.data.PublicHearingDataService;
import gov.nysenate.openleg.service.hearing.data.PublicHearingNotFoundEx;
import gov.nysenate.openleg.service.hearing.search.PublicHearingSearchService;
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

@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class PublicHearingGetCtrl extends BaseCtrl
{
    @Autowired
    private PublicHearingDataService hearingData;

    @Autowired
    private PublicHearingSearchService hearingSearch;

    /**
     * Public Hearing Listing API
     * --------------------------
     *
     * Retrieve all public hearings: (GET) /api/3/hearings/
     * Request Parameters : sort - Lucene syntax for sorting by any field of a public hearing response.
     *                      full - If true, the full public hearing view is returned. Otherwise just its filename.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     *
     * Expected Output: List of PublicHearingView or PublicHearingIdView.
     */
    @RequestMapping(value = "")
    public BaseResponse getAllHearings(@RequestParam(defaultValue = "date:desc") String sort,
                                       @RequestParam(defaultValue = "false") boolean full,
                                       WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<PublicHearingId> results = hearingSearch.searchPublicHearings(sort, limOff);
        return ListViewResponse.of(results.getResults().stream().map(r ->
                        (full) ? new PublicHearingView(hearingData.getPublicHearing(r.getResult()))
                                : new PublicHearingIdView(r.getResult()))
                        .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    /**
     * Public Hearing Listing API.
     * --------------------------
     *
     * Retrieve public hearings for a year: (GET) /api/3/hearings/{year}
     * Request Parameters : sort - Lucene syntax for sorting by any field of a public hearing response.
     *                      full - If true, the full public hearing view is returned. Otherwise just its filename.
     *                      limit - Limit the number of results
     *                      offset - Start results from an offset.
     *
     * Expected Output: List of PublicHearingIdView or PublicHearingView.
     */
    @RequestMapping(value = "/{year:[\\d]{4}}")
    public BaseResponse getHearingsByYear(@PathVariable int year,
                                          @RequestParam(defaultValue = "date:desc") String sort,
                                          @RequestParam(defaultValue = "false") boolean full,
                                          WebRequest webRequest)
                                          throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        SearchResults<PublicHearingId> results = hearingSearch.searchPublicHearings(year, sort, limOff);
        return ListViewResponse.of(results.getResults().stream().map(r ->
                        (full) ? new PublicHearingView(hearingData.getPublicHearing(r.getResult()))
                                : new PublicHearingIdView(r.getResult()))
                        .collect(Collectors.toList()), results.getTotalResults(), limOff);
    }

    /**
     * Single Public Hearing Retrieval API.
     * ------------------------------------
     *
     * Retrieve a singe public hearing by its filename.
     * (GET) /api/3/hearings/{filename}
     *
     * Request Parameters: None
     *
     * Expected Output: PublicHearingView
     *
     */
    @RequestMapping(value = "/{filename:.*}")
    public BaseResponse getHearing(@PathVariable String filename) {
        return new ViewObjectResponse<>(
                new PublicHearingView(hearingData.getPublicHearing(new PublicHearingId(filename))),
        "Data for public hearing " + filename);
    }

    /**
     * Single Public Hearing PDF retrieval API.
     * ----------------------------------------
     *
     * Retrieve a single public hearing text pdf: (GET) /api/3/hearings/{filename}.pdf
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping(value = "/{filename}.pdf")
    public void getHearingPdf(@PathVariable String filename, HttpServletResponse response)
            throws IOException, COSVisitorException {
        PublicHearing hearing = hearingData.getPublicHearing(new PublicHearingId(filename));
        new PublicHearingPdfView(hearing, response.getOutputStream());
        response.setContentType("application/pdf");
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
