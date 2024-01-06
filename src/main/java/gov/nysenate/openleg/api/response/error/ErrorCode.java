package gov.nysenate.openleg.api.response.error;


/**
 * Represents the error codes returned by the OpenLeg 3.0 API.
 */
public enum ErrorCode {
    /** --- Basic --- */
    UNKNOWN_ERROR(-1, "There was an unexpected error while handling your request."),
    INVALID_ARGUMENTS(1, "One or more of the provided request parameters was not valid"),
    MISSING_PARAMETERS(2, "A required parameter was missing from the request"),

    /** --- Missing data --- */
    BILL_NOT_FOUND(11, "The requested bill was not found"),
    BILL_AMENDMENT_NOT_FOUND(12, "The requested bill amendment was not found"),
    LAW_DOC_NOT_FOUND(21, "The requested law document was not found"),
    LAW_TREE_NOT_FOUND(22, "The requested law tree was not found"),
    SPOTCHECK_REPORT_NOT_FOUND(31, "The requested spotcheck report was not found"),
    SENATE_SITE_JSON_DUMP_MISSING_FIELDS(32, "The JSON dump is missing required fields"),
    COMMITTEE_NOT_FOUND(41, "The requested committee was not found"),
    COMMITTEE_VERSION_NOT_FOUND(42, "The requested committee version was not found"),
    CALENDAR_NOT_FOUND(51, "The requested calendar was not found"),
    AGENDA_NOT_FOUND(61, "The requested agenda was not found"),

    /** --- Other --- */
    SEARCH_ERROR(100, "There was error retrieving your search results. Make sure that the query is valid."),
    IMMUTABLE_ENV_VARIABLE(110, "Attempt to change an immutable environment variable"),
    NO_SUCH_ENV_VARIABLE(111, "The requested environment variable does not exist"),
    PROCESS_RUN_NOT_FOUND(121, "The given data process run was not found"),
    DATA_PROCESS_RUN_FAILED(122, "The data process did not run, due to processing being disabled or an error"),
    SOURCE_FILE_NOT_FOUND(131, "The source file was not found"),

    /** --- Other missing data --- */
    TRANSCRIPT_NOT_FOUND(141, "The transcript was not found"),
    DUPLICATE_TRANSCRIPT(142, "There are two transcripts at this dateTime. Please specify."),
    MEMBER_NOT_FOUND(151, "The member was not found"),
    NOTIFICATION_NOT_FOUND(161, "The requested notification was not found"),
    HEARING_NOT_FOUND(171, "The requested hearing was not found"),

    /** --- Admin --- */

    USER_ALREADY_EXISTS(191, "The entered username already exists"),
    USER_DOES_NOT_EXIST(192, "The entered username is not currently registered as a user"),
    SAME_PASSWORD(193, "The new password cannot match the existing password"),
    CANNOT_DELETE_ADMIN(194, "The default administrator cannot be deleted"),
    EMAIL_IN_USE(200, "This email is already in use"),

    /** --- General --- */

    UNAUTHORIZED(401, "Not authorized to perform this request."),
    INVALID_DATE_RANGE(601, "The supplied date range is not valid."),
    API_KEY_REQUIRED(701, "A valid API key is needed to fulfill this request."),
    API_KEY_INVALID(702, "Sorry, the API key you provided is not valid.")
    ;

    private final int code;
    /** Description of return code and possible corrective actions. */
    private final String message;

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
}
