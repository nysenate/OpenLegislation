package gov.nysenate.openleg.controller.api.base;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.request.ParameterView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BaseCtrl.class);

    public static final String BASE_API_PATH = "/api/3";

    /** --- Param grabbers --- */

    /**
     * Returns a sort order extracted from the given web request parameters
     * Returns the given default sort order if no such parameter exists
     * @param webRequest
     * @param defaultSortOrder
     * @return
     */
    protected SortOrder getSortOrder(WebRequest webRequest, SortOrder defaultSortOrder) {
        try {
            return SortOrder.valueOf(webRequest.getParameter("order"));
        }
        catch (Exception ex) {
            return defaultSortOrder;
        }
    }

    /**
     * Returns a limit + offset extracted from the given web request parameters
     * Returns the given default limit offset if no such parameters exist
     *
     * @param webRequest
     * @param defaultLimitOffset
     * @return LimitOffset
     */
    protected LimitOffset getLimitOffset(WebRequest webRequest, LimitOffset defaultLimitOffset) {
        try {
            if (webRequest.getParameter("limit").equalsIgnoreCase("all")) {
                return LimitOffset.ALL;
            }
            if (!webRequest.getParameterMap().containsKey("offset")) {
                return new LimitOffset(Integer.parseInt(webRequest.getParameter("limit")));
            }
            return new LimitOffset(Integer.parseInt(webRequest.getParameter("limit")),
                    Integer.parseInt(webRequest.getParameter("offset")));
        }
        catch (Exception ex) {
            return defaultLimitOffset;
        }
    }

    /**
     * Extracts a date range from the query parameters 'startDate' and 'endDate'.
     *
     * @param webRequest
     * @param defaultRange
     * @return Range<LocalDate>
     */
    protected Range<LocalDate> getDateRange(WebRequest webRequest, Range<LocalDate> defaultRange) {
        try {
            LocalDate startDate = null;
            LocalDate endDate = null;
            if (webRequest.getParameterMap().containsKey("startDate")) {
                startDate = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(webRequest.getParameter("startDate")));
            }
            if (webRequest.getParameterMap().containsKey("endDate")) {
                endDate = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(webRequest.getParameter("endDate")));
            }
            return (startDate == null && endDate == null)
                ? defaultRange
                : Range.closed(startDate != null ? startDate : DateUtils.LONG_AGO,
                                 endDate != null ? endDate   : DateUtils.THE_FUTURE);
        }
        catch (Exception ex) {
            return defaultRange;
        }
    }

    /** --- Generic Exception Handlers --- */

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse handleUnknownError(Exception ex) {
        logger.error("Caught unhandled servlet exception:\n{}", ExceptionUtils.getStackTrace(ex));
        return new ErrorResponse(ErrorCode.UNKNOWN_ERROR);
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleTypeMismatchException(TypeMismatchException ex) {
        return new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleInvalidArgumentEx(IllegalArgumentException ex) {
        return new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMissingParameterException(MissingServletRequestParameterException ex) {
        return new ViewObjectErrorResponse(ErrorCode.MISSING_PARAMETERS,
            new ParameterView(ex.getParameterName(), ex.getParameterType()));
    }

    @ExceptionHandler(SearchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ViewObjectErrorResponse searchExceptionHandler(SearchException ex) {
        logger.warn("Search Exception: {}\n{}", ex.getMessage(), ExceptionUtils.getStackTrace(ex.getCause()));
        return new ViewObjectErrorResponse(ErrorCode.SEARCH_ERROR, ex.getMessage());
    }
}