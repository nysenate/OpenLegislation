package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.client.view.transcript.TranscriptPdfView;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.service.transcript.data.TranscriptDataService;
import org.apache.pdfbox.exceptions.COSVisitorException;
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
    @Autowired
    private TranscriptDataService transcriptData;

    /**
     * Single Transcript PDF retrieval API
     * -----------------------------------
     *
     * Retrieve a single transcript text pdf: (GET) /api/3/transcripts/{filename}.pdf
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping("/{filename}")
    public void getTranscriptPdf(@PathVariable String filename,
                                 HttpServletResponse response) throws IOException, COSVisitorException {
        TranscriptId transcriptId = new TranscriptId(filename);
        Transcript transcript = transcriptData.getTranscript(transcriptId);
        new TranscriptPdfView(transcript, response.getOutputStream());
        response.setContentType("application/pdf");
    }
}