package gov.nysenate.openleg.api.legislation.law;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.RetiredRequestParamEx;
import gov.nysenate.openleg.api.legislation.law.view.*;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.law.*;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.legislation.law.dao.LawDocumentNotFoundEx;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_API_PATH + "/laws", method = RequestMethod.GET)
public class LawGetCtrl extends BaseCtrl {
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
        LimitOffset limOff = getLimitOffset(webRequest, 1000);
        List<LawInfo> lawInfoList = lawDataService.getLawInfos();
        ListViewResponse<LawInfoView> response = ListViewResponse.of(
                LimitOffset.limitList(lawInfoList.stream().map(LawInfoView::new).toList(), limOff),
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
        response.setMessage("Law document for location " + locationId + " in " + lawId + " law.");
        return response;
    }

    /**
     * Repealed Law API
     * ----------------
     *
     * Gets the latest version of each law document whose repeal LBDC published on a date in the given range.
     *
     * Usage:
     * (GET) /api/3/laws/repealed
     *
     * Request Params:
     * @param fromDate iso date - optional - The first repeal published date to include
     * @param toDate iso date - optional - The repeal published date to stop before (exclusive)
     * @return {@link ListViewResponse<RepealedLawDocIdView>}
     */
    @RequestMapping("/repealed")
    public ListViewResponse<RepealedLawDocIdView> getRepealedLaws(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            WebRequest request) {
        rejectRetiredRepealedParam("fromDateTime", request);
        rejectRetiredRepealedParam("toDateTime", request);
        Optional<LocalDate> from = parseOptionalISODate(fromDate, "fromDate");
        Optional<LocalDate> to = parseOptionalISODate(toDate, "toDate");
        Range<LocalDate> dateRange;
        if (from.isPresent() && to.isPresent()) {
            dateRange = getClosedOpenRange(from.get(), to.get(), "fromDate", "toDate");
        } else if (from.isPresent()) {
            dateRange = Range.atLeast(from.get());
        } else {
            dateRange = to.map(Range::lessThan).orElse(Range.all());
        }
        List<RepealedLawDocId> repealedLawDocs = lawDataService.getRepealedLawDocs(dateRange);
        return repealedLawDocs.stream()
                .map(RepealedLawDocIdView::new)
                .collect(collectingAndThen(toList(), ListViewResponse::of));
    }

    /** An empty value counts as omitted. */
    private static Optional<LocalDate> parseOptionalISODate(String value, String parameter) {
        return Optional.ofNullable(value).filter(v -> !v.isEmpty()).map(v -> parseISODate(v, parameter));
    }

    /** fromDateTime and toDateTime (toDate inclusive) were replaced by fromDate and toDate (toDate exclusive). */
    private static void rejectRetiredRepealedParam(String parameter, WebRequest request) {
        String[] values = request.getParameterValues(parameter);
        if (values == null) {
            return;
        }
        throw new RetiredRequestParamEx(String.join(",", values), parameter, "date",
                "No longer supported. Use fromDate and toDate.",
                parameter + " is no longer supported on repealed laws. Use fromDate and toDate (ISO dates) " +
                "instead. toDate is exclusive: repeals published on toDate are not included.");
    }

    /** --- Exception Handlers --- */

    @ExceptionHandler(LawTreeNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleLawTreeNotFoundEx(LawTreeNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.LAW_TREE_NOT_FOUND, new LawIdQueryView(ex.getLawId(), ex.getEndPubDate()));
    }

    @ExceptionHandler(LawDocumentNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleLawDocNotFoundEx(LawDocumentNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.LAW_DOC_NOT_FOUND, new LawDocQueryView(ex.getDocId(), ex.getEndPublishedDate()));
    }
}
