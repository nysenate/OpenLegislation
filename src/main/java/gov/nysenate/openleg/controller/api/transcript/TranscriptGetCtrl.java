package gov.nysenate.openleg.controller.api.transcript;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.transcript.TranscriptIdView;
import gov.nysenate.openleg.client.view.transcript.TranscriptView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.service.transcript.data.TranscriptDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(BASE_API_PATH + "/transcripts")
public class TranscriptGetCtrl extends BaseCtrl
{
    @Autowired
    private TranscriptDataService transcriptDataService;

    @RequestMapping("/{year:[\\d]{4}}")
    public BaseResponse getTranscriptsByYear(@PathVariable int year,
                                             @RequestParam(defaultValue = "desc") String sort,
                                             @RequestParam(defaultValue = "false") boolean full,
                                             WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, LimitOffset.FIFTY);
        return ListViewResponse.of(
            transcriptDataService.getTranscriptIds(year, SortOrder.DESC, limOff).stream()
                .map(tid -> new TranscriptIdView(tid))
                .collect(Collectors.toList()), 0, limOff);
    }

    @RequestMapping("/{type}/{dateTime}")
    public BaseResponse getTranscript(@PathVariable String type,
                                      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        return new ViewObjectResponse<>(
            new TranscriptView(transcriptDataService.getTranscript(new TranscriptId(type.toUpperCase(), dateTime)))
        );
    }
}
