package gov.nysenate.openleg.controller.pdf;

import gov.nysenate.openleg.client.view.bill.BillPdfView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.service.bill.data.BillAmendNotFoundEx;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/pdf/bills")
public class BillPdfCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillPdfCtrl.class);

    @Autowired protected BillDataService billData;

    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}")
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
                Set<BillTextFormat> fullTextFormats = getFullTextFormats(request);
                Bill bill = billData.getBill(BaseBillId.of(billId), fullTextFormats);
                ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
                BillPdfView.writeBillPdf(bill, billId.getVersion(), pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/pdf"));
                return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
            }
        } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        } catch (IOException | URISyntaxException | COSVisitorException ex) {
            logger.error("Exception in bill pdf viewer.", ex);
            response.sendError(404, "PDF text for " + printNo + " " + sessionYear + " is not available.");
        }
        return null;
    }
}
