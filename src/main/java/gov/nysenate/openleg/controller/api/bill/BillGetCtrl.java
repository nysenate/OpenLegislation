package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.client.view.bill.SimpleBillView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    public List<BillInfo> getBills(@PathVariable int sessionYear,
                                         @RequestParam(defaultValue = "50") int limit,
                                         @RequestParam(defaultValue = "1") int offset) {
        LimitOffset limOff = new LimitOffset(limit, offset);
        return billDataService.getBillIds(SessionYear.of(sessionYear), limOff).parallelStream()
            .map(billId -> billDataService.getBillInfo(billId))
            .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public Bill getBill(@PathVariable String sessionYear, @PathVariable String printNo, HttpServletRequest req) {
        return billDataService.getBill(new BaseBillId(printNo, Integer.parseInt(sessionYear)));
    }
}
