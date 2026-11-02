package gov.nysenate.openleg.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Controls the retirement of options that expand collection responses into full content.
 * In {@link Mode#OFF} every option keeps its existing behavior. In {@link Mode#ENFORCE}
 * a retired option that is set to true is rejected.
 * The mode applies to every caller and only changes on restart.
 */
@Component
public class CollectionFullPolicy {
    public static final String MODE_PROPERTY = "api.collection_full.mode";

    public enum Mode {
        OFF, ENFORCE
    }

    /**
     * A retired option. Its message and constraint end with a link to the migration guide,
     * which is resolved against {@code domain.url}.
     */
    public enum Option {
        BILL_FULL("full", "/static/docs/html/migration.html#bill-collection-full-migration",
                "Bill search and listing no longer return full bills. Omit full for bill summaries, " +
                "or use idsOnly=true for bill IDs, then request each bill from " +
                "/api/3/bills/{sessionYear}/{printNo}.",
                "Must be false or omitted on bill search and listing."),
        BILL_UPDATES_FULL_BILL("fullBill", "/static/docs/html/migration.html#bill-updates-fullbill-migration",
                "Bill update feeds no longer return full bills. Use summary=true for bill summaries, " +
                "or use the bill IDs in the update tokens to request each bill from " +
                "/api/3/bills/{sessionYear}/{printNo}.",
                "Must be false or omitted on bill update feeds."),
        HEARING_FULL("full", "/static/docs/html/migration.html#hearing-collection-full-migration",
                "Hearing search and listing no longer return full hearings. Omit full and use summary=true " +
                "for hearing details without the text, or summary=false for IDs only, then request each " +
                "hearing from /api/3/hearings/{id}.",
                "Must be false or omitted on hearing search and listing."),
        TRANSCRIPT_FULL("full", "/static/docs/html/migration.html#transcript-collection-full-migration",
                "Transcript search and listing no longer return full transcripts. Omit full and use " +
                "summary=true for transcript details without the text, or summary=false for IDs only, then " +
                "request each transcript from /api/3/transcripts/{dateTime}/{sessionType}.",
                "Must be false or omitted on transcript search and listing."),
        CALENDAR_FULL("full", "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Calendar listings no longer return full calendars. Omit full for calendar summaries, " +
                "then request each calendar from /api/3/calendars/{year}/{calendarNumber}.",
                "Must be false or omitted on calendar listings."),
        ACTIVE_LIST_FULL("full", "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Active list listings no longer return full active lists. Omit full for active list " +
                "summaries, then request each active list from " +
                "/api/3/calendars/{year}/{calendarNumber}/{sequenceNumber}.",
                "Must be false or omitted on active list listings."),
        SUPPLEMENTAL_FULL("full", "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Supplemental calendar listings no longer return full supplementals. Omit full for " +
                "supplemental summaries, then request each one from " +
                "/api/3/calendars/{year}/{calendarNumber}/{version}.",
                "Must be false or omitted on supplemental calendar listings."),
        CALENDAR_SEARCH_DETAIL("detail", "/static/docs/html/migration.html#calendar-collection-full-migration",
                "Calendar search no longer returns full calendars. Omit detail for calendar summaries, " +
                "then request each calendar from /api/3/calendars/{year}/{calendarNumber}.",
                "Must be false or omitted on calendar search."),
        AGENDA_SEARCH_FULL("full", "/static/docs/html/migration.html#agenda-search-full-migration",
                "Agenda search no longer returns full committee agendas. Omit full for committee agenda " +
                "summaries, then request each one from /api/3/agendas/{year}/{agendaNo}/{committeeName}.",
                "Must be false or omitted on agenda search.");

        private final String parameterName;
        private final String guidePath;
        private final String message;
        private final String constraint;

        Option(String parameterName, String guidePath, String message, String constraint) {
            this.parameterName = parameterName;
            this.guidePath = guidePath;
            this.message = message;
            this.constraint = constraint;
        }

        public String getParameterName() {
            return parameterName;
        }
    }

    private final Mode mode;
    private final String domainUrl;

    public CollectionFullPolicy(@Value("${" + MODE_PROPERTY + ":off}") String mode,
                                @Value("${domain.url}") String domainUrl) {
        this.mode = switch (mode.trim()) {
            case "off" -> Mode.OFF;
            case "enforce" -> Mode.ENFORCE;
            default -> throw new IllegalArgumentException(
                    "Invalid " + MODE_PROPERTY + " '" + mode + "': must be off or enforce");
        };
        this.domainUrl = domainUrl;
    }

    public Mode getMode() {
        return mode;
    }

    /** @return the response message for a rejected option, including the migration guide link */
    public String getMessage(Option option) {
        return option.message + " Migration guide: " + getGuideUrl(option);
    }

    /** @return the parameter constraint for a rejected option, including the migration guide link */
    public String getConstraint(Option option) {
        return option.constraint + " Migration guide: " + getGuideUrl(option);
    }

    private String getGuideUrl(Option option) {
        return domainUrl + option.guidePath;
    }

    /**
     * Rejects a retired option when the mode is enforce and its value is true.
     *
     * @param option Option - the retired option
     * @param value boolean - the option's value as the handler reads it (Spring binding or
     *              {@link BaseCtrl#getBooleanParam}, whichever the endpoint already uses)
     * @param request WebRequest - supplies the raw value(s) sent
     * @throws RetiredRequestParamEx if the option is rejected
     */
    public void check(Option option, boolean value, WebRequest request) {
        if (mode != Mode.ENFORCE || !value) {
            return;
        }
        String[] values = request.getParameterValues(option.parameterName);
        String receivedValue = values == null ? null : String.join(",", values);
        throw new RetiredRequestParamEx(receivedValue, option.parameterName, "boolean",
                getConstraint(option), getMessage(option));
    }
}
