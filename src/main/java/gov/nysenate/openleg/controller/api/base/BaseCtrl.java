package gov.nysenate.openleg.controller.api.base;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.error.InvalidParameterView;
import gov.nysenate.openleg.client.view.request.ParameterView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
     * @param defaultLimit - The default limit to use, 0 for no limit
     * @return LimitOffset
     */
    protected LimitOffset getLimitOffset(WebRequest webRequest, int defaultLimit) {
        int limit = defaultLimit;
        int offset = 0;
        if (webRequest.getParameter("limit") != null) {
            if (webRequest.getParameter("limit").equalsIgnoreCase("all")) {
                limit = 0;
            }
            else {
                limit = NumberUtils.toInt(webRequest.getParameter("limit"), defaultLimit);
            }
        }
        if (webRequest.getParameter("offset") != null) {
            offset = NumberUtils.toInt(webRequest.getParameter("offset"), 0);
        }
        return new LimitOffset(limit, offset);
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

    /**
     * Attempts to parse a date time request parameter
     * Throws an InvalidRequestParameterException if the parsing went wrong
     * @param dateTimeString The parameter value to be parsed
     * @param parameterName The name of the parameter.  Used to generate the exception
     * @return
     * @throws InvalidRequestParameterException
     */
    protected LocalDateTime parseISODateTimeParameter(String dateTimeString, String parameterName)
            throws InvalidRequestParameterException {
        try {
            return LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(dateTimeString));
        }
        catch (DateTimeParseException ex) {
            throw new InvalidRequestParameterException(dateTimeString, parameterName,
                    "date-time", "ISO 8601 date and time formatted string e.g. 2014-10-27T09:44:55");
        }
    }

    /** --- Generic Exception Handlers --- */

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse handleUnknownError(Exception ex) {
        logger.error("Caught unhandled servlet exception:\n{}", ExceptionUtils.getStackTrace(ex));
        return new ErrorResponse(ErrorCode.UNKNOWN_ERROR);
    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleInvalidRequestParameterException(InvalidRequestParameterException ex) {
        logger.debug(ExceptionUtils.getStackTrace(ex));
        return new ViewObjectErrorResponse(ErrorCode.INVALID_ARGUMENTS, new InvalidParameterView(ex));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMissingParameterException(MissingServletRequestParameterException ex) {
        logger.debug(ExceptionUtils.getStackTrace(ex));
        return new ViewObjectErrorResponse(ErrorCode.MISSING_PARAMETERS,
            new ParameterView(ex.getParameterName(), ex.getParameterType()));
    }

    @ExceptionHandler(SearchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ViewObjectErrorResponse searchExceptionHandler(SearchException ex) {
        logger.debug("Search Exception!", ex);
        return new ViewObjectErrorResponse(ErrorCode.SEARCH_ERROR, ex.getMessage());
    }
}