package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillText;
import gov.nysenate.openleg.processor.bill.BillTextDiffProcessor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

/**
 * A temporary api endpoint which will be used to initialize
 * TextDiff data from our saved full_text_html data.
 *
 * Be sure to disable db triggers before running to prevent lots of entries in the bills updates api.
 */
@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/textdiff")
public class TextDiffInitCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TextDiffInitCtrl.class);

    @Autowired private BillDao sqlBilldao;
    @Autowired private BillTextDiffProcessor textDiffProcessor;

    @RequiresPermissions("admin")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse initTextDiffs() {
        List<SessionYear> sessionYears = Arrays.asList(new SessionYear(2017), new SessionYear(2019));

        logger.info("Starting to convert full_html_text into text diffs.");

        for (SessionYear sessionYear : sessionYears) {
            List<BaseBillId> baseBillIds = sqlBilldao.getBillIds(sessionYear, LimitOffset.ALL, SortOrder.NONE);
            for (BaseBillId id : baseBillIds) {
                logger.info("Converting bill " + id.toString() + " to text diff format.");
                Bill bill = sqlBilldao.getBill(id);
                logger.info("Converting bill " + bill.getBaseBillId().toString() + " to text diff format.");
                for (BillAmendment amend : bill.getAmendmentList()) {
                    String xmlFullText = sqlBilldao.getXmlFullText(amend.getBillId());
                    BillText billText = textDiffProcessor.processBillText(xmlFullText);
                    amend.setBillText(billText);
                    sqlBilldao.updateBillAmendText(amend);
                }
            }
        }

        return new SimpleResponse(true, "Done initializing textdiff's.", "");
    }
}
