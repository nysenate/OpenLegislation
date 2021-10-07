package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.PublicHearingPdfView;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/pdf/hearings")
public class PublicHearingPdfCtrl {
    private static final Logger logger = LoggerFactory.getLogger(PublicHearingPdfCtrl.class);

    @Autowired
    private PublicHearingDataService hearingData;

    /**
     * Single Public Hearing PDF retrieval.
     * Retrieve a single public hearing text pdf:
     * (GET) /pdf/hearings/{filename}/ or (GET) /pdf/hearings/{id}/
     * Request Parameters: None.
     * Expected Output: PDF response.
     */
    @RequestMapping(value = "/{identifier}")
    public ResponseEntity<byte[]> getHearingPdf(@PathVariable String identifier, HttpServletResponse response)
            throws IOException {
        try {
            PublicHearing hearing;
            try {
                hearing = hearingData.getPublicHearing(new PublicHearingId(Integer.parseInt(identifier)));
            }
            catch (NumberFormatException ex) {
                hearing = hearingData.getPublicHearing(identifier);
            }
            return new PublicHearingPdfView(hearing).writeData();
        } catch (PublicHearingNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        } catch (IOException ex) {
            logger.error("Failed to return transcript PDF", ex);
            response.sendError(404, ex.getMessage());
        }
        return null;
    }
}
