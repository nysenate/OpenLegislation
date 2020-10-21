package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingPdfView;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingNotFoundEx;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping(value = "/pdf/hearings")
public class PublicHearingPdfCtrl
{

    private static final Logger logger = LoggerFactory.getLogger(PublicHearingPdfCtrl.class);

    @Autowired
    private PublicHearingDataService hearingData;

    /**
     * Single Public Hearing PDF retrieval.
     * -----------------------------------
     *
     * Retrieve a single public hearing text pdf: (GET) /pdf/hearings/{filename}/
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping(value = "/{filename}")
    public ResponseEntity<byte[]> getHearingPdf(@PathVariable String filename, HttpServletResponse response)
            throws IOException {
        try {
            PublicHearing hearing = hearingData.getPublicHearing(new PublicHearingId(filename));
            ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
            PublicHearingPdfView.writePublicHearingPdf(hearing, pdfBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
        } catch (PublicHearingNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        } catch (COSVisitorException ex) {
            logger.error("Failed to return transcript PDF", ex);
            response.sendError(404, ex.getMessage());
        }
        return null;
    }
}
