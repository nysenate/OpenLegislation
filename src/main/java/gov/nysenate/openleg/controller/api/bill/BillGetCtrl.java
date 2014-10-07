package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleErrorResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET)
public class BillGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillGetCtrl.class);

    @Autowired
    private BillDataService billDataService;

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}")
    public BaseResponse getBills(@PathVariable int sessionYear,
                                 @RequestParam MultiValueMap<String, String> parameters) {
        LimitOffset limOff = getLimitOffset(parameters, LimitOffset.FIFTY);
        try {
            return ListViewResponse.of(
                    billDataService.getBillIds(SessionYear.of(sessionYear), limOff).parallelStream()
                        .map(billId -> new BillInfoView(billDataService.getBillInfo(billId)))
                        .collect(Collectors.toList()),
                    billDataService.getBillCount(SessionYear.of(sessionYear)),
                    limOff
            );
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get bills by session");
        }
    }

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public BaseResponse getBill(@PathVariable int sessionYear, @PathVariable String printNo) {
        try {
            return new ViewObjectResponse<>( new BillView(
                    billDataService.getBill(new BaseBillId(printNo, sessionYear))));
        }
        catch (BillNotFoundEx ex) {
            return new SimpleErrorResponse(
                    String.format("Could not find a bill with printNo: %s for session year: %s", printNo, sessionYear));
        }
        catch (IllegalArgumentException ex) {
            return new SimpleErrorResponse(
                    String.format("Illegal print no %s", printNo));
        }
        catch (Exception ex) {
            return handleRequestException(logger, ex, "get bill");
        }
    }
}
