package gov.nysenate.openleg.controller.pdf;

import gov.nysenate.openleg.client.view.hearing.PublicHearingPdfView;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.service.hearing.data.PublicHearingDataService;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/pdf/hearings")
public class PublicHearingPdfCtrl
{
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
    public void getHearingPdf(@PathVariable String filename, HttpServletResponse response)
            throws IOException, COSVisitorException {
        PublicHearing hearing = hearingData.getPublicHearing(new PublicHearingId(filename));
        new PublicHearingPdfView(hearing, response.getOutputStream());
        response.setContentType("application/pdf");
    }
}
