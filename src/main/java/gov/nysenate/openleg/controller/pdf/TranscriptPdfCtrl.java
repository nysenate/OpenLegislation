package gov.nysenate.openleg.controller.pdf;

import gov.nysenate.openleg.client.view.transcript.TranscriptPdfView;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptNotFoundEx;
import gov.nysenate.openleg.service.transcript.data.TranscriptDataService;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/pdf/transcripts")
public class TranscriptPdfCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TranscriptPdfCtrl.class);

    @Autowired
    private TranscriptDataService transcriptData;

    /**
     * Single Transcript PDF retrieval
     * -------------------------------
     *
     * Retrieve a single transcript text pdf: (GET) /pdf/transcripts/{filename}/
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping("/{filename}")
    public void getTranscriptPdf(@PathVariable String filename,
                                 HttpServletResponse response) throws IOException, COSVisitorException {
        TranscriptId transcriptId = new TranscriptId(filename);
        try {
            Transcript transcript = transcriptData.getTranscript(transcriptId);
            new TranscriptPdfView(transcript, response.getOutputStream());
            response.setContentType("application/pdf");
        }
        catch (TranscriptNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        }
        catch (Exception ex) {
            logger.warn("Failed to return transcript PDF", ex);
        }
    }
}