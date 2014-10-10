package gov.nysenate.openleg.controller.api.base;

import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.service.base.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

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
     * @param webRequest
     * @param defaultLimitOffset
     * @return
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

    /** --- Generic Exception Handlers --- */

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleInvalidArgumentEx(IllegalArgumentException ex) {
        return new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse handleUnknownError(Exception ex) {
        logger.error("Caught unhandled servlet exception:");
        logger.error(ex.toString());
        // Print first 5 lines of stack trace
        for(int i=0; i<5 && i<ex.getStackTrace().length; i++) {
            logger.error("    " + ex.getStackTrace()[i].toString());
        }
        return new ErrorResponse(ErrorCode.UNKNOWN_ERROR);
    }

    @ExceptionHandler(SearchException.class)
    @ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
    public ViewObjectErrorResponse searchExceptionHandler(SearchException ex) {
        return new ViewObjectErrorResponse(ErrorCode.SEARCH_ERROR, ex.getMessage());
    }
}