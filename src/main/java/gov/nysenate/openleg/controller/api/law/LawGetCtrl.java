package gov.nysenate.openleg.controller.api.law;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.law.*;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawInfo;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawTreeNode;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.data.LawDocumentNotFoundEx;
import gov.nysenate.openleg.service.law.data.LawTreeNotFoundEx;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_API_PATH + "/laws", method = RequestMethod.GET)
public class LawGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(LawGetCtrl.class);

    @Autowired private LawDataService lawDataService;

    /** --- Request Handlers --- */

    /**
     * Law Listing API
     * ---------------
     *
     * Get a listing of available laws.
     *
     * Usage
     * (GET) /api/3/laws
     *
     * Expected output: List of LawInfoView
     */
    @RequestMapping("")
    public BaseResponse getLaws(WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 0);
        List<LawInfo> lawInfoList = lawDataService.getLawInfos();
        ListViewResponse<LawInfoView> response = ListViewResponse.of(
            LimitOffset.limitList(lawInfoList.stream().map(li -> new LawInfoView(li)).collect(toList()), limOff),
            lawInfoList.size(), limOff);
        response.setMessage("Listing of consolidated and unconsolidated NYS Laws");
        return response;
    }

    /**
     * Law Tree API
     * ------------
     *
     * Retrieves a law tree with various options to control the resulting output.
     *
     * Usage
     * (GET) /api/3/laws/{lawId}
     *
     * Optional Params:
     * date (iso date) - The published date of the law tree (defaults to latest law tree)
     * fromLocation (string) - Start the law tree at a certain node based on location id.
     * depth (integer) - Output child nodes up to the specified depth (defaults to the full depth of the tree)
     * full (boolean) - If set to true all document text will also be fetched. (defaults to no document text)
     *
     * Expected output: LawTreeView
     */
    @RequestMapping("/{lawId}")
    public BaseResponse getLawTree(@PathVariable String lawId, @RequestParam(required = false) String date,
                                   @RequestParam(required = false) String fromLocation,
                                   @RequestParam(required = false) Integer depth,
                                   @RequestParam(defaultValue = "false") boolean full) {
        LocalDate publishedDate = (date != null) ? parseISODate(date, "date") : null;
        LawTree lawTree = lawDataService.getLawTree(lawId, publishedDate);
        ViewObjectResponse<LawTreeView> response =
            (full) ? new ViewObjectResponse<>(new LawTreeView(lawTree, fromLocation, depth,
                                                              lawDataService.getLawDocuments(lawId, publishedDate)))
                   : new ViewObjectResponse<>(new LawTreeView(lawTree, fromLocation, depth));
        response.setMessage("The document structure for " + lawId + " law");
        return response;
    }

    /**
     * Law Document API
     * ----------------
     *
     * Retrieves a law document (with an optional active date).
     *
     * Usage
     * (GET) /api/3/laws/{lawId}/{locationId}
     *
     * Optional Params:
     * date (iso date) - Published date of the document (defaults to latest).
     * refTreeDate (iso date) - Published date of the containing law tree (defaults to latest).
     *
     * Expected output: LawDocWithRefsView
     */
    @RequestMapping("/{lawId}/{locationId}")
    public BaseResponse getLawDocument(@PathVariable String lawId, @PathVariable String locationId,
                                       @RequestParam(required = false) String date,
                                       @RequestParam(required = false) String refTreeDate) {
        LocalDate activeDate = (date != null) ? parseISODate(date, "date") : LocalDate.now();
        String documentId = lawId + locationId;
        LawDocument doc = lawDataService.getLawDocument(documentId, activeDate);
        LocalDate refTreeLocalDate = (refTreeDate != null) ? parseISODate(refTreeDate, "refTreeDate") : LocalDate.now();
        Optional<LawTreeNode> lawTreeNodeOpt = lawDataService.getLawTree(lawId, refTreeLocalDate).find(documentId);
        ViewObjectResponse<LawDocWithRefsView> response = new ViewObjectResponse<>(new LawDocWithRefsView(doc, lawTreeNodeOpt));
        response.setMessage("Law document for location " + locationId + " in " + lawId + " law ");
        return response;
    }

    /** --- Exception Handlers --- */

    @ExceptionHandler(LawTreeNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleLawTreeNotFoundEx(LawTreeNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.LAW_DOC_NOT_FOUND, new LawIdQueryView(ex.getLawId(), ex.getEndPubDate()));
    }

    @ExceptionHandler(LawDocumentNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleLawDocNotFoundEx(LawDocumentNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.LAW_DOC_NOT_FOUND, new LawDocQueryView(ex.getDocId(), ex.getEndPublishedDate()));
    }
}