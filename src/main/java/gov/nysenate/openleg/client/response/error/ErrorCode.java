package gov.nysenate.openleg.client.response.error;


import java.util.HashMap;
import java.util.Map;

/**
 * Represents the error codes returned by the OpenLeg 3.0 API.
 */
public enum ErrorCode
{
    /** --- Base --- */

    UNKNOWN_ERROR(-1, "There was an unexpected error while handling your request."),

    INVALID_ARGUMENTS(1, "One or more of the provided request parameters was not valid"),
    MISSING_PARAMETERS(2, "A required parameter was missing from the request"),

    /** --- Bill --- */

    BILL_NOT_FOUND(11, "The requested bill was not found"),

    /** --- Law --- */

    LAW_DOC_NOT_FOUND(21, "The requested law document was not found"),
    LAW_TREE_NOT_FOUND(22, "The requested law tree was not found"),

    /** --- Spotcheck --- */

    SPOTCHECK_REPORT_NOT_FOUND(31, "The requested spotcheck report was not found"),

    /** --- Committee --- */

    COMMITTEE_NOT_FOUND(41, "The requested committee was not found"),
    COMMITTEE_VERSION_NOT_FOUND(42, "The requested committee version was not found"),

    /** --- Calendar --- */

    CALENDAR_NOT_FOUND(51, "The requested calendar was not found"),
    INVALID_CAL_SEARCH_PARAMS(52, "The given calendar search parameters are invalid/conflicting"),

    /** --- General --- */

    SEARCH_ERROR(100, "There was error retrieving your search results. Make sure that the query is valid.")

    ;

    /** Used for lookups by code. */

    private static final Map<Integer, String> codeMap = new HashMap<>();
    static {
        for (ErrorCode rc : ErrorCode.values()) {
            codeMap.put(rc.code, rc.name());
        }
    }

    /** Numerical return code. */
    private int code = 0;

    /** Description of return code and possible corrective actions. */
    private String message = "";

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns the ErrorCode using the numerical code.
     * @param code int
     * @return ErrorCode if code matches, null otherwise.
     */
    public static ErrorCode getByCode(int code) {
        if (codeMap.containsKey(code)) {
            return ErrorCode.valueOf(codeMap.get(code));
        }
        return null;
    }
}
