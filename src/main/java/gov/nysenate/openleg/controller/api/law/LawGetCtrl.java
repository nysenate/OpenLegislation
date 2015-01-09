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
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.data.LawDocumentNotFoundEx;
import gov.nysenate.openleg.service.law.data.LawTreeNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_API_PATH + "/laws", method = RequestMethod.GET)
public class LawGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(LawGetCtrl.class);

    @Autowired private LawDataService lawDataService;

    /** --- Request Handlers --- */

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

    @RequestMapping("/{lawId}")
    public BaseResponse getLawTree(@PathVariable String lawId, @RequestParam(required = false) String date,
                                   @RequestParam(defaultValue = "false") boolean full) {
        LocalDate publishedDate = (date == null) ? LocalDate.now() : parseISODate(date, "date");
        LawTree lawTree = lawDataService.getLawTree(lawId, publishedDate);
        ViewObjectResponse<LawTreeView> response =
            (full) ? new ViewObjectResponse<>(new LawTreeView(lawTree, lawDataService.getLawDocuments(lawId, publishedDate)))
                   : new ViewObjectResponse<>(new LawTreeView(lawTree));
        response.setMessage("The document structure for " + lawId + " law");
        return response;
    }

    @RequestMapping("/{lawId}/{locationId}")
    public BaseResponse getLawDocument(@PathVariable String lawId, @PathVariable String locationId,
                                       @RequestParam(required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        LawDocument doc = lawDataService.getLawDocument(lawId + locationId, date);
        ViewObjectResponse<LawDocView> response = new ViewObjectResponse<>(new LawDocView(doc));
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