package gov.nysenate.openleg.controller.api.hearing;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.hearing.PublicHearingUpdateTokenView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.hearing.PublicHearingDao;
import gov.nysenate.openleg.model.hearing.PublicHearingUpdateToken;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/hearings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class PublicHearingUpdatesCtrl extends BaseCtrl
{

    @Autowired private PublicHearingDao publicHearingDao;

    /**
     * Public Hearing Updates API.
     * -----------------
     *
     * Returns a List of public hearing ids that have been inserted or updated on or after the supplied date.
     * Usage: (GET) /api/3/hearings/updates/{from datetime}
     *
     * Request Params:  limit - Limit the number of results
     *                  offset - Start results from an offset.
     *
     * Expected Output: List of PublicHearingUpdateTokenView
     */
    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getNewPublicHearingsSince(@PathVariable String from,
                                                  WebRequest request) {
        return getNewPublicHearingsDuring(parseISODateTime(from, "from"), DateUtils.THE_FUTURE.atStartOfDay(), request);
    }

    /**
     * Public Hearing Updates API.
     *  -----------------
     *
     * Returns a list of public hearing ids that have been inserted or updated during a supplied date time range.
     * Usage: (GET) /api/3/hearings/updates/{from datetime}/{to datetime}
     *
     * Request Params:  limit - Limit the number of results
     *                  offset - Start results from an offset.
     *
     * Expected Output: List of PublicHearingUpdateTokenView
     */
    @RequestMapping(value = "/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getNewPublicHearingsDuring(@PathVariable String from,
                                                   @PathVariable String to,
                                                   WebRequest request) {
        return getNewPublicHearingsDuring(parseISODateTime(from, "from"), parseISODateTime(to, "to"), request);
    }

    /** --- Internal Methods --- */

    private BaseResponse getNewPublicHearingsDuring(LocalDateTime from, LocalDateTime to, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 25);
        Range<LocalDateTime> dateRange = getOpenRange(from, to, "from", "to");
        PaginatedList<PublicHearingUpdateToken> updates = publicHearingDao.publicHearingsUpdatedDuring(dateRange, SortOrder.ASC, limOff);
        return ListViewResponse.of(updates.getResults().stream()
                .map(PublicHearingUpdateTokenView::new)
                .collect(Collectors.toList()), updates.getTotal(), limOff);
    }
}
