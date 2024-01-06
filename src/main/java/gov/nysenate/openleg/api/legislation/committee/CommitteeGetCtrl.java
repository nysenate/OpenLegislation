package gov.nysenate.openleg.api.legislation.committee;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeIdView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeSessionIdView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeVersionIdView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/committees", method = RequestMethod.GET)
public class CommitteeGetCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(CommitteeGetCtrl.class);
    private final CommitteeDataService committeeDataService;

    @Autowired
    public CommitteeGetCtrl(CommitteeDataService committeeDataService) {
        this.committeeDataService = committeeDataService;
    }

    /** --- Request Handlers --- */

    /**
     * Current Committee API
     *
     * Retrieve the latest version of a single committee for the specified session year:
     *                          (GET) /api/3/committees/{sessionYear}/{chamber}/{committeeName}
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}/{committeeName}")
    public BaseResponse getLatestCommitteeForSession(@PathVariable String chamberName,
                                                     @PathVariable String committeeName,
                                                     @PathVariable int sessionYear)
            throws CommitteeNotFoundEx {
        return getCommitteeResponse(
                committeeDataService.getCommittee(new CommitteeSessionId(
                        Chamber.getValue(chamberName), committeeName, getSessionYearParam(sessionYear, "sessionYear")))
        );
    }

    /**
     * Committee Version API
     *
     * Gets the state of a committee as it existed at a given reference date/time:
     *                          (GET) /api/3/committees/{sessionYear}/{chamber}/{committeeName}/{referenceDateTime}
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}/{committeeName}/{referenceDateTime}")
    public BaseResponse getCommitteeAtTime(@PathVariable String chamberName,
                                           @PathVariable String committeeName,
                                           @PathVariable int sessionYear,
                                           @PathVariable String referenceDateTime)
        throws CommitteeNotFoundEx, InvalidRequestParamEx {
        LocalDateTime parsedReferenceDateTime = parseISODateTime(referenceDateTime, "referenceDateTime");
        return getCommitteeResponse(
                committeeDataService.getCommittee(new CommitteeVersionId(
                        Chamber.getValue(chamberName), committeeName,
                        getSessionYearParam(sessionYear, "sessionYear"), parsedReferenceDateTime))
        );
    }

    /**
     * Current Committee List API
     *
     * Retrieve the latest versions of all committees for a given chamber for a given session year:
     *                          (GET) /api/3/committees/{sessionYear}/{chamber}
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}")
    public BaseResponse getCommitteesForChamber(@PathVariable String chamberName,
                                                @PathVariable int sessionYear,
                                                @RequestParam(defaultValue = "false") boolean full,
                                                WebRequest webRequest) {
        LimitOffset limitOffset = getLimitOffset(webRequest, 50);
        Chamber chamber = Chamber.getValue(chamberName);
        SessionYear session = getSessionYearParam(sessionYear, "sessionYear");
        return ListViewResponse.of(
                committeeDataService.getCommitteeList(chamber, session, limitOffset).stream()
                        .map(committee -> full ? new CommitteeView(committee)
                                               : new CommitteeVersionIdView(committee.getVersionId()) )
                        .toList(),
                committeeDataService.getCommitteeListCount(chamber, session), limitOffset );
    }

    /**
     * Committee History API
     *
     * Get all versions of the given committee for the given session year:
     *                          (GET) /api/3/committees/{sessionYear}/{chamber}/{committeeName}
     *
     * Request Parameters:      full - Set to true to retrieve full committee responses (false by default)
     *                          limit - Limit the number of results (default 50)
     *                          offset - Start results from offset (default 1)
     *                          order - Determines the creation date sort order of the versions (default DESC)
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}/{committeeName}/history")
    public BaseResponse getCommitteeHistory(@PathVariable String chamberName,
                                            @PathVariable String committeeName,
                                            @PathVariable int sessionYear,
                                            @RequestParam(defaultValue = "false") boolean full,
                                            WebRequest webRequest) throws CommitteeNotFoundEx {
        LimitOffset limitOffset = getLimitOffset(webRequest, 50);
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.DESC);
        SessionYear session = getSessionYearParam(sessionYear, "sessionYear");
        CommitteeSessionId committeeSessionId =
                new CommitteeSessionId(Chamber.getValue(chamberName), committeeName, session);
        List<Committee> history = committeeDataService.getCommitteeHistory(committeeSessionId, limitOffset, sortOrder);
        int totalCount = committeeDataService.getCommitteeHistoryCount(committeeSessionId);
        return getCommitteeListResponse(history, totalCount, limitOffset, full);
    }

    /** --- Exception Handlers --- */

    @ExceptionHandler(CommitteeNotFoundEx.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ViewObjectErrorResponse handleCommitteeNotFoundEx(CommitteeNotFoundEx ex) {
        logger.debug(ExceptionUtils.getStackTrace(ex));
        if (ex.getCommitteeId() instanceof CommitteeVersionId) {
            return new ViewObjectErrorResponse(ErrorCode.COMMITTEE_VERSION_NOT_FOUND,
                    new CommitteeVersionIdView((CommitteeVersionId) ex.getCommitteeId()));
        } else if (ex.getCommitteeId() instanceof CommitteeSessionId) {
            return new ViewObjectErrorResponse(ErrorCode.COMMITTEE_VERSION_NOT_FOUND,
                    new CommitteeSessionIdView((CommitteeSessionId) ex.getCommitteeId()));
        } else {
            return new ViewObjectErrorResponse(ErrorCode.COMMITTEE_NOT_FOUND, new CommitteeIdView(ex.getCommitteeId()));
        }
    }

    /**
     * --- Internal Methods ---
     */

    protected BaseResponse getCommitteeResponse(Committee committee) {
        return new ViewObjectResponse<>(new CommitteeView(committee));
    }

    protected BaseResponse getCommitteeListResponse(Collection<Committee> committeeCollection, int totalCount,
                                                    LimitOffset limitOffset, boolean full) {
        return ListViewResponse.of(
                committeeCollection.stream()
                        .map(committee -> full ? new CommitteeView(committee)
                                : new CommitteeVersionIdView(committee.getVersionId()) )
                        .toList(),
                totalCount, limitOffset
        );
    }
}
