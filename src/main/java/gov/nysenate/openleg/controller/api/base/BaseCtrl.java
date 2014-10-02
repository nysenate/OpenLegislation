package gov.nysenate.openleg.controller.api.base;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleErrorResponse;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import org.slf4j.Logger;
import org.springframework.util.MultiValueMap;

public abstract class BaseCtrl
{
    public static final String BASE_API_PATH = "/api/3";

    protected SortOrder getSortOrder(MultiValueMap<String, String> parameters, SortOrder defaultSortOrder) {
        try {
            return SortOrder.valueOf(parameters.getFirst("order"));
        }
        catch (Exception ex) {
            return defaultSortOrder;
        }
    }

    protected LimitOffset getLimitOffset(MultiValueMap<String, String> parameters, LimitOffset defaultLimitOffset) {
        try {
            if (parameters.getFirst("limit").equalsIgnoreCase("all")) {
                return LimitOffset.ALL;
            }
            if (!parameters.containsKey("offset")) {
                return new LimitOffset(Integer.parseInt(parameters.getFirst("limit")));
            }
            return new LimitOffset(Integer.parseInt(parameters.getFirst("limit")),
                    Integer.parseInt(parameters.getFirst("offset")));
        }
        catch (Exception ex) {
            return defaultLimitOffset;
        }
    }

    protected BaseResponse handleRequestException(Logger logger, Exception ex, String requestType) {
        logger.error(String.format("Caught unhandled exception for request %s:", requestType));
        logger.error(ex.toString());
        // Print first 5 lines of stack trace
        for(int i=0; i<5 && i<ex.getStackTrace().length; i++) {
            logger.error("    " + ex.getStackTrace()[i].toString());
        }
        return new SimpleErrorResponse(String.format("Could not process %s request", requestType));
    }
}
