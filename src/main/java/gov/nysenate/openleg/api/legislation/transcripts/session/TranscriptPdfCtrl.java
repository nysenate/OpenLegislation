package gov.nysenate.openleg.api.legislation.transcripts.session;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfView;
import gov.nysenate.openleg.legislation.transcripts.session.DuplicateTranscriptEx;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/pdf/transcripts")
public class TranscriptPdfCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(TranscriptPdfCtrl.class);
    private final TranscriptDataService transcriptData;

    @Autowired
    public TranscriptPdfCtrl(TranscriptDataService transcriptData) {
        this.transcriptData = transcriptData;
    }

    /**
     * Single Transcript PDF retrieval
     * -------------------------------
     *
     * Retrieve a single transcript text pdf: (GET) /pdf/transcripts/{dateTime}/{optional sessionType}
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */

    @RequestMapping("/{dateTime}")
    public ResponseEntity<byte[]> getTranscriptPdfByDateTime(@PathVariable String dateTime, HttpServletResponse response)
            throws IOException {
        return pdfHelper(dateTime, null, response);
    }

    @RequestMapping("/{dateTime}/{sessionType}")
    public ResponseEntity<byte[]> getTranscriptPdf(@PathVariable String dateTime, @PathVariable String sessionType, HttpServletResponse response)
            throws IOException {
        return pdfHelper(dateTime, sessionType, response);
    }

    private ResponseEntity<byte[]> pdfHelper(String dateTime, String sessionType,
                                                    HttpServletResponse response) throws IOException {
        LocalDateTime ldt = parseISODateTime(dateTime, "dateTime");
        try {
            Transcript transcript = sessionType == null ?
                    transcriptData.getTranscriptByDateTime(ldt) :
                    transcriptData.getTranscript(new TranscriptId(ldt, sessionType));
            return new TranscriptPdfView(transcript).writeData();
        }
        catch (DuplicateTranscriptEx | TranscriptNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        } catch (IOException ex) {
            logger.error("Failed to return transcript PDF", ex);
            response.sendError(404, ex.getMessage());
        }
        return null;
    }
}
