package gov.nysenate.openleg.api.updates.law;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.law.view.LawDocIdView;
import gov.nysenate.openleg.api.legislation.law.view.LawVersionIdView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.DateRangeListViewResponse;
import gov.nysenate.openleg.api.updates.view.UpdateDigestView;
import gov.nysenate.openleg.api.updates.view.UpdateTokenView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawVersionId;
import gov.nysenate.openleg.updates.UpdateDigest;
import gov.nysenate.openleg.updates.UpdateToken;
import gov.nysenate.openleg.updates.UpdateType;
import gov.nysenate.openleg.updates.law.LawUpdatesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/laws", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class LawUpdatesCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(LawUpdatesCtrl.class);

    @Autowired private LawUpdatesDao lawUpdatesDao;

    /**
     * Law Document Updates API (for all laws)
     * ------------------------------
     *
     * Usages:
     * (GET) /api/3/laws/updates/  (last 7 days)
     * (GET) /api/3/laws/updates/{from date-time}
     * (GET) /api/3/laws/updates/{from date-time}/{to date-time}
     *
     * Request Params: detail (boolean) - Show update digests within each token.
     *                 type (string) - Update type (processed, published) Default: published
     *                 limit, offset (int) - Paginate
     *                 order (string) - Order by update date
     *
     * Expected Response: List of UpdateTokenView<LawVersionId> or UpdateDigestTokenView<LawDocId> if detail = true
     */
    @RequestMapping(value = "/updates")
    public BaseResponse getAllUpdates(WebRequest request) {
        return getAllUpdates(LocalDateTime.now().minusDays(7), LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}")
    public BaseResponse getAllUpdates(@PathVariable String from, WebRequest request) {
        return getAllUpdates(parseISODateTime(from, "from"), LocalDateTime.now(), request);
    }

    @RequestMapping(value = "/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getAllUpdates(@PathVariable String from, @PathVariable String to, WebRequest request) {
        return getAllUpdates(parseISODateTime(from, "from"), parseISODateTime(to, "to"), request);
    }

    /**
     * Law Tree Updates API
     * ------------------------------
     *
     * Usages:
     * (GET) /api/3/laws/tree/updates
     * (GET) /api/3/laws/tree/updates/{from date-time}
     * (GET) /api/3/laws/tree/updates/{from date-time}/{to date-time}
     *
     * date parameters default to 1970-01-01 and now respectively
     *
     * Request Params: type (string) - Update type (processed, published) Default: published
     *                 limit, offset (int) - Paginate
     *                 order (string) - Order by update date
     *
     * Expected Response: List of UpdateTokenView<LawVersionId>
     */
    @RequestMapping(value = "/tree/updates")
    public BaseResponse getLawTreeUpdates(WebRequest request) {
        return getLawTreeUpdates(null, null, request);
    }

    @RequestMapping(value = "/tree/updates/{from:.*\\.?.*}")
    public BaseResponse getLawTreeUpdates(@PathVariable String from,
                                          WebRequest request) {
        return getLawTreeUpdates(from, null, request);
    }

    @RequestMapping(value = "/tree/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getLawTreeUpdates(@PathVariable String from,
                                          @PathVariable String to,
                                          WebRequest request) {

        LocalDateTime parsedFromDateTime = Optional.ofNullable(from)
                .map(tdt -> parseISODateTime(tdt, "from"))
                .orElse(DateUtils.LONG_AGO);
        LocalDateTime parsedToDateTime = Optional.ofNullable(to)
                .map(tdt -> parseISODateTime(tdt, "to"))
                .orElse(LocalDateTime.now());

        BaseLawUpdatesParams params = getBaseParams(parsedFromDateTime, parsedToDateTime, request);

        PaginatedList<UpdateToken<LawVersionId>> lawTreeUpdates =
                lawUpdatesDao.getLawTreeUpdates(params.updateRange, params.updateType, params.sortOrder, params.limOff);

        return getTokenListResponse(params, lawTreeUpdates);
    }

    /**
     * Law Updates API (for a specific law)
     * ------------------------------------
     *
     * Usages:
     * (GET) /api/3/laws/{lawId}updates/
     * (GET) /api/3/laws/{lawId}/updates/{from date-time}
     * (GET) /api/3/laws/{lawId}/updates/{from date-time}/{to date-time}
     *
     * Where lawId is the three letter id for the law (e.g. ABC or EDN).
     * @see #getAllUpdates for request params and output.
     */
    @RequestMapping(value = "/{lawId:[\\w]{3}}/updates")
    public BaseResponse getUpdatesForLaw(@PathVariable String lawId, WebRequest request) {
        return getUpdatesForLaw(lawId, DateUtils.LONG_AGO, DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/{lawId:[\\w]{3}}/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesForLaw(@PathVariable String lawId, @PathVariable String from, WebRequest request) {
        return getUpdatesForLaw(lawId, parseISODateTime(from, "from"), DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/{lawId:[\\w]{3}}/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesForLaw(@PathVariable String lawId, @PathVariable String from, @PathVariable String to,
                                         WebRequest request) {
        return getUpdatesForLaw(lawId, parseISODateTime(from, "from"), parseISODateTime(to, "to"), request);
    }

    /**
     * Law Updates API (for a specific document)
     * -----------------------------------------
     *
     * Usages:
     * (GET) /api/3/laws/{lawId}/{locationId}/updates/
     * (GET) /api/3/laws/{lawId}/{locationId}/updates/{from date-time}
     * (GET) /api/3/laws/{lawId}/{locationId}/updates/{from date-time}/{to date-time}
     *
     * Where locationId is the 'locationId' in {@link LawDocId}.
     * @see #getAllUpdates for request params and output.
     */
    @RequestMapping(value = "/{lawId}/{locationId}/updates")
    public BaseResponse getUpdatesForLawDoc(@PathVariable String lawId, @PathVariable String locationId, WebRequest request) {
        return getUpdatesForLawDoc(lawId, locationId, DateUtils.LONG_AGO,
                                   DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/{lawId}/{locationId}/updates/{from:.*\\.?.*}")
    public BaseResponse getUpdatesForLawDoc(@PathVariable String lawId, @PathVariable String locationId,
                                            @PathVariable String from, WebRequest request) {
        return getUpdatesForLawDoc(lawId, locationId, parseISODateTime(from, "from"),
                                   DateUtils.THE_FUTURE, request);
    }

    @RequestMapping(value = "/{lawId}/{locationId}/updates/{from:.*\\.?.*}/{to:.*\\.?.*}")
    public BaseResponse getUpdatesForLawDoc(@PathVariable String lawId, @PathVariable String locationId,
                                            @PathVariable String from, @PathVariable String to, WebRequest request) {
        return getUpdatesForLawDoc(lawId, locationId, parseISODateTime(from, "from"), parseISODateTime(to, "to"), request);
    }

    /** --- Internal --- */

    private BaseResponse getAllUpdates(LocalDateTime from, LocalDateTime to, WebRequest request) {
        BaseLawUpdatesParams params = getBaseParams(from, to, request);
        if (!params.detail) {
            PaginatedList<UpdateToken<LawVersionId>> updateTokens =
                lawUpdatesDao.getUpdates(params.updateRange, params.updateType, params.sortOrder, params.limOff);
            return getTokenListResponse(params, updateTokens);
        }
        else {
            PaginatedList<UpdateDigest<LawDocId>> updateDigests =
                lawUpdatesDao.getDetailedUpdates(params.updateRange, params.updateType, params.sortOrder, params.limOff);
            return getDigestListResponse(params, updateDigests);
        }
    }

    private BaseResponse getUpdatesForLaw(String lawId, LocalDateTime from, LocalDateTime to, WebRequest request) {
        BaseLawUpdatesParams params = getBaseParams(from, to, request);
        PaginatedList<UpdateDigest<LawDocId>> updateDigests =
            lawUpdatesDao.getDetailedUpdatesForLaw(lawId, params.updateRange, params.updateType,
                                                   params.sortOrder, params.limOff);
        return getDigestListResponse(params, updateDigests);
    }

    private BaseResponse getUpdatesForLawDoc(String lawId, String locationId, LocalDateTime from, LocalDateTime to,
                                             WebRequest request) {
        BaseLawUpdatesParams params = getBaseParams(from, to, request);
        String docId = lawId + locationId;
        PaginatedList<UpdateDigest<LawDocId>> updateDigests =
            lawUpdatesDao.getDetailedUpdatesForDocument(docId, params.updateRange, params.updateType,
                    params.sortOrder, params.limOff);
        return getDigestListResponse(params, updateDigests);
    }

    private BaseResponse getTokenListResponse(BaseLawUpdatesParams params, PaginatedList<UpdateToken<LawVersionId>> updateTokens) {
        return DateRangeListViewResponse.of(updateTokens.results().stream()
                .map(token -> new UpdateTokenView(token, new LawVersionIdView(token.getId())))
                .toList(), params.updateRange, updateTokens.total(), params.limOff);
    }

    private BaseResponse getDigestListResponse(BaseLawUpdatesParams params, PaginatedList<UpdateDigest<LawDocId>> updateDigests) {
        return DateRangeListViewResponse.of(updateDigests.results().stream()
            .map(digest -> new UpdateDigestView(digest, new LawDocIdView(digest.getId())))
            .toList(), params.updateRange, updateDigests.total(), params.limOff);
    }

    private static class BaseLawUpdatesParams {
        LimitOffset limOff;
        Range<LocalDateTime> updateRange;
        SortOrder sortOrder;
        UpdateType updateType;
        boolean detail;
    }

    private BaseLawUpdatesParams getBaseParams(LocalDateTime from, LocalDateTime to, WebRequest request) {
        BaseLawUpdatesParams params = new BaseLawUpdatesParams();
        params.limOff = getLimitOffset(request, 50);
        params.updateRange = getClosedOpenRange(from, to, "from", "to");
        params.sortOrder = getSortOrder(request, SortOrder.ASC);
        params.updateType = getUpdateTypeFromParam(request);
        params.detail = getBooleanParam(request, "detail", false);
        return params;
    }
}
