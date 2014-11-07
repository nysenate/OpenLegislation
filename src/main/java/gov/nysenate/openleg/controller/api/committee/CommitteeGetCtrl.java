package gov.nysenate.openleg.controller.api.committee;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.client.view.committee.CommitteeSessionIdView;
import gov.nysenate.openleg.client.view.committee.CommitteeVersionIdView;
import gov.nysenate.openleg.client.view.committee.CommitteeView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParameterException;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@RestController
@RequestMapping(value = BASE_API_PATH + "/committees", method = RequestMethod.GET)
public class CommitteeGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(CommitteeGetCtrl.class);

    @Autowired
    CommitteeDataService committeeDataService;

    /** --- Request Handlers --- */

    /**
     * Returns the latest committee version with the given name for the specified chamber during the given session year
     * @param chamberName
     * @param committeeName
     * @return
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}/{committeeName}")
    public BaseResponse getLatestCommitteeForSession(@PathVariable String chamberName,
                                                     @PathVariable String committeeName,
                                                     @PathVariable int sessionYear)
            throws CommitteeNotFoundEx {
        return new ViewObjectResponse<>(new CommitteeView(
                committeeDataService.getCommittee(new CommitteeSessionId(Chamber.getValue(chamberName),
                                                                            committeeName, SessionYear.of(sessionYear)))
        ));
    }

    /**
     * Returns the committee version corresponding to the given committee version id
     *      consisting of chamber, committee name, session year and reference date
     * @param chamberName
     * @param committeeName
     * @param sessionYear
     * @param referenceDateTime
     * @return
     * @throws CommitteeNotFoundEx
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}/{committeeName}/{referenceDateTime}")
    public BaseResponse getCommitteeAtTime(@PathVariable String chamberName,
                                           @PathVariable String committeeName,
                                           @PathVariable int sessionYear,
                                           @PathVariable String referenceDateTime)
        throws CommitteeNotFoundEx, InvalidRequestParameterException {
        LocalDateTime parsedReferenceDateTime = parseISODateTimeParameter(referenceDateTime, "referenceDateTime");
        return new ViewObjectResponse<>(new CommitteeView(
                committeeDataService.getCommittee(new CommitteeVersionId(Chamber.getValue(chamberName), committeeName,
                        SessionYear.of(sessionYear), parsedReferenceDateTime))
        ));
    }

    /**
     * Returns the current committee version for each committee in the given chamber
     * @param chamberName
     * @param full
     * @param webRequest
     * @return
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}")
    public BaseResponse getCommitteesForChamber(@PathVariable String chamberName,
                                                @PathVariable int sessionYear,
                                                @RequestParam(defaultValue = "false") boolean full,
                                                WebRequest webRequest) {
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.FIFTY);
        Chamber chamber = Chamber.getValue(chamberName);
        SessionYear session = SessionYear.of(sessionYear);
        return ListViewResponse.of(
                committeeDataService.getCommitteeList(chamber, session, limitOffset).stream()
                        .map(committee -> full ? new CommitteeView(committee)
                                               : new CommitteeVersionIdView(committee.getVersionId()) )
                        .collect(Collectors.toList()),
                committeeDataService.getCommitteeListCount(chamber, session), limitOffset );
    }

    /**
     * Returns all of the previous committee versions for the given committee id ordered by session year and creation date
     * @param chamberName
     * @param committeeName
     * @param full
     * @param webRequest
     * @return
     * @throws CommitteeNotFoundEx
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/{chamberName:(?i)senate|assembly}/{committeeName}/history")
    public BaseResponse getCommitteeHistory(@PathVariable String chamberName,
                                            @PathVariable String committeeName,
                                            @PathVariable int sessionYear,
                                            @RequestParam(defaultValue = "false") boolean full,
                                            WebRequest webRequest) throws CommitteeNotFoundEx {
        LimitOffset limitOffset = getLimitOffset(webRequest, LimitOffset.FIFTY);
        SortOrder sortOrder = getSortOrder(webRequest, SortOrder.DESC);
        CommitteeSessionId committeeSessionId =
                new CommitteeSessionId(Chamber.getValue(chamberName), committeeName, SessionYear.of(sessionYear));
        return ListViewResponse.of(
                committeeDataService.getCommitteeHistory(committeeSessionId, limitOffset, sortOrder).stream()
                        .map(committee -> full ? new CommitteeView(committee)
                                               : new CommitteeVersionIdView(committee.getVersionId()) )
                        .collect(Collectors.toList()),
                committeeDataService.getCommitteeHistoryCount(committeeSessionId), limitOffset);
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
}
