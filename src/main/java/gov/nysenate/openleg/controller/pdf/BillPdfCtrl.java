package gov.nysenate.openleg.controller.pdf;

import gov.nysenate.openleg.client.view.bill.BillPdfView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@RestController
@RequestMapping(value = "/pdf/bills")
public class BillPdfCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillPdfCtrl.class);

    @Autowired protected BillDataService billData;

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
    public void getBillPdf(@PathVariable int sessionYear, @PathVariable String printNo,
                           HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            BillId billId = new BillId(printNo, sessionYear);
            Optional<String> alternateUrl = billData.getAlternateBillPdfUrl(billId);
            if (alternateUrl.isPresent()) {
                String urlString = alternateUrl.get();
                URI url = new URI(urlString);
                if (!url.isAbsolute()) {
                    urlString = request.getContextPath() + urlString;
                }
                response.sendRedirect(urlString);
            }
            else {
                Bill bill = billData.getBill(BaseBillId.of(billId));
                new BillPdfView(bill, billId.getVersion(), response.getOutputStream());
                response.setContentType("application/pdf");
            }
        }
        catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        }
        catch (Exception ex) {
            logger.error("Exception in bill pdf viewer.", ex);
            response.sendError(404, "PDF text for " + printNo + " " + sessionYear + " is not available.");
        }
    }
}
