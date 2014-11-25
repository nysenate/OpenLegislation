package gov.nysenate.openleg.controller.api.transcript;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.transcript.TranscriptIdView;
import gov.nysenate.openleg.client.view.transcript.TranscriptPdfView;
import gov.nysenate.openleg.client.view.transcript.TranscriptView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.service.transcript.data.TranscriptDataService;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

/**
 * Transcript retrieval APIs.
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/transcripts", method = RequestMethod.GET)
public class TranscriptGetCtrl extends BaseCtrl
{
    @Autowired
    private TranscriptDataService transcriptDataService;

    /**
     * Transcript Listing API.
     *
     * Retrieve transcripts for a year: (GET) /api/3/transcripts/{year}
     *
     */
    @RequestMapping("/{year:[\\d]{4}}")
    public BaseResponse getTranscriptsByYear(@PathVariable int year,
                                             @RequestParam(defaultValue = "desc") String sort,
                                             @RequestParam(defaultValue = "false") boolean full,
                                             WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        return ListViewResponse.of(
            transcriptDataService.getTranscriptIds(year, SortOrder.DESC, limOff).stream()
                .map(tid -> new TranscriptIdView(tid))
                .collect(Collectors.toList()), 0, limOff);
    }

    /**
     * Single Transcript Retrieval API.
     *
     * Retrieve a single transcripts by its type and dateTime: (GET) /api/3/transcripts/{type}/{dateTime} <p>
     *              i.e. api/3/transcripts/REGULAR SESSION/2014-09-12T13:00</p>
     *
     * <p>Request Parameters: None.</p>
     *
     */
    @RequestMapping("/{type}/{dateTime}")
    public BaseResponse getTranscript(@PathVariable String type,
                                      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        return new ViewObjectResponse<>(
            new TranscriptView(transcriptDataService.getTranscript(new TranscriptId(type.toUpperCase(), dateTime)))
        );
    }

    /**
     * Single Transcript PDF retrieval API.
     *
     * Retrieve a single transcript text pdf: (GET) /api/3/transcripts/{type}/{dateTime}.pdf
     *
     * Request Parameters: None.
     *
     * Expected Output: PDF response.
     */
    @RequestMapping("/{type}/{dateTime}.pdf")
    public void getTranscriptPdf(@PathVariable String type,
                                 @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime dateTime,
                                 HttpServletResponse response) throws IOException, COSVisitorException {

        TranscriptId transcriptId = new TranscriptId(type.toUpperCase(), dateTime);
        Transcript transcript = transcriptDataService.getTranscript(transcriptId);
        new TranscriptPdfView(transcript, response.getOutputStream());
        response.setContentType("application/pdf");
    }
}
