package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingPdfView;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingNotFoundEx;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
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
public class HearingPdfCtrl {
    private static final Logger logger = LoggerFactory.getLogger(HearingPdfCtrl.class);
    private final HearingDataService hearingData;

    @Autowired
    public HearingPdfCtrl(HearingDataService hearingData) {
        this.hearingData = hearingData;
    }

    /**
     * Single Hearing PDF retrieval.
     * Retrieve a single hearing text pdf:
     * (GET) /pdf/hearings/{filename}/ or (GET) /pdf/hearings/{id}/
     * Request Parameters: None.
     * Expected Output: PDF response.
     */
    @RequestMapping(value = "/{identifier}")
    public ResponseEntity<byte[]> getHearingPdf(@PathVariable String identifier, HttpServletResponse response)
            throws IOException {
        try {
            Hearing hearing;
            try {
                hearing = hearingData.getHearing(new HearingId(Integer.parseInt(identifier)));
            }
            catch (NumberFormatException ex) {
                hearing = hearingData.getHearing(identifier);
            }
            return new HearingPdfView(hearing).writeData();
        } catch (HearingNotFoundEx ex) {
            response.sendError(404, ex.getMessage());
        } catch (IOException ex) {
            logger.error("Failed to return transcript PDF", ex);
            response.sendError(404, ex.getMessage());
        }
        return null;
    }
}
