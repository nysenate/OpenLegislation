package gov.nysenate.openleg.controller.pdf;

import gov.nysenate.openleg.client.view.transcript.TranscriptPdfView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptNotFoundEx;
import gov.nysenate.openleg.service.transcript.data.TranscriptDataService;
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
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/pdf/transcripts")
public class TranscriptPdfCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TranscriptPdfCtrl.class);

    @Autowired
    private TranscriptDataService transcriptData;

    /**
     * Single Transcript PDF retrieval
     * -------------------------------
     *
     * Retrieve a single transcript text pdf: (GET) /pdf/transcripts/{dateTime}/
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping("/{dateTime}")
    public ResponseEntity<byte[]> getTranscriptPdf(@PathVariable String dateTime, HttpServletResponse response)
            throws IOException {
        LocalDateTime localDateTime = parseISODateTime(dateTime, "dateTime");
        TranscriptId transcriptId = new TranscriptId(localDateTime);
        try {
            Transcript transcript = transcriptData.getTranscript(transcriptId);
            ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
            TranscriptPdfView.writeTranscriptPdf(transcript, pdfBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
        }
        catch (TranscriptNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        }
        catch (COSVisitorException ex) {
            logger.error("Failed to return transcript PDF", ex);
            response.sendError(404, ex.getMessage());
        }
        return null;
    }
}