package gov.nysenate.openleg.controller.api.law;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleErrorResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.law.LawDocView;
import gov.nysenate.openleg.client.view.law.LawInfoView;
import gov.nysenate.openleg.client.view.law.LawTreeView;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_API_PATH + "/laws", method = RequestMethod.GET)
public class LawGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(LawGetCtrl.class);

    @Autowired
    private LawDataService lawDataService;

    @RequestMapping("")
    public BaseResponse getLaws(@RequestParam MultiValueMap<String, String> parameters) {
        LimitOffset limOff = getLimitOffset(parameters, LimitOffset.ALL);
        List<LawInfo> lawInfoList = lawDataService.getLawInfos();
        ListViewResponse<LawInfoView> response = ListViewResponse.of(
            LimitOffset.limitList(lawInfoList.stream().map(li -> new LawInfoView(li)).collect(toList()), limOff),
            lawInfoList.size(), limOff);
        response.setMessage("Listing of consolidated and unconsolidated NYS Laws");
        return response;
    }

    @RequestMapping("/{lawId}")
    public BaseResponse getLawTree(@PathVariable String lawId,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        try {
            LawTree lawTree = lawDataService.getLawTree(lawId, date);
            ViewObjectResponse<LawTreeView> response = new ViewObjectResponse<>(new LawTreeView(lawTree));
            response.setMessage("The document structure for " + lawId + " law");
            return response;
        }
        catch (LawTreeNotFoundEx ex) {
            return new SimpleErrorResponse("No law match was found for law id " + lawId);
        }

    }

    @RequestMapping("/{lawId}/{locationId}")
    public BaseResponse getLawDocument(@PathVariable String lawId, @PathVariable String locationId,
                                       @RequestParam(required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        try {
            LawDocument doc = lawDataService.getLawDocument(lawId + locationId, date);
            ViewObjectResponse<LawDocView> response = new ViewObjectResponse<>(new LawDocView(doc));
            response.setMessage("Law document for location " + locationId + " in " + lawId + " law ");
            return response;
        }
        catch (LawDocumentNotFoundEx ex) {
            return new SimpleErrorResponse("No " + lawId + " law document with location id " + locationId + " and " +
                                           "active date " + date + " exists.");
        }
    }
}