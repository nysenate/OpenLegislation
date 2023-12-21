package gov.nysenate.openleg.api.legislation.bill;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.bill.view.BillPdfView;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.bill.exception.BillAmendNotFoundEx;
import gov.nysenate.openleg.legislation.bill.exception.BillNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/pdf/bills")
public class BillPdfCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(BillPdfCtrl.class);
    private final BillDataService billData;

    @Autowired
    public BillPdfCtrl(BillDataService billData) {
        this.billData = billData;
    }

    @RequestMapping(value = "/{sessionYear:\\d{4}}/{printNo}")
    public ResponseEntity<byte[]> getBillPdf(@PathVariable int sessionYear, @PathVariable String printNo,
                                             WebRequest request, HttpServletResponse response)
            throws IOException {
        try {
            BillId billId = getBillId(printNo, sessionYear, "printNo");
            Optional<String> alternateUrl = billData.getAlternateBillPdfUrl(billId);
            if (alternateUrl.isPresent()) {
                String urlString = alternateUrl.get();
                URI url = new URI(urlString);
                if (!url.isAbsolute()) {
                    urlString = request.getContextPath() + urlString;
                }
                response.sendRedirect(urlString);
            } else {
                Bill bill = billData.getBill(BaseBillId.of(billId));
                return new BillPdfView(bill, billId.getVersion()).writeData();
            }
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        } catch (IOException | URISyntaxException ex) {
            logger.error("Exception in bill pdf viewer.", ex);
            response.sendError(404, "PDF text for " + printNo + " " + sessionYear + " is not available.");
        }
        return null;
    }
}
