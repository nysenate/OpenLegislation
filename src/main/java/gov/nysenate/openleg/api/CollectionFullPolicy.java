package gov.nysenate.openleg.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Controls the retirement of options that expand collection responses into full content.
 * In {@link Mode#OFF} every option keeps its existing behavior. In {@link Mode#ENFORCE}
 * a retired option that is set to true is rejected. Handlers check it as their first step,
 * before validating other parameters or reading any data.
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
        BILL_FULL("full", "/static/docs/html/bills.html#bill-collection-full-migration",
                "Bill search and listing no longer return full bills. Omit full for bill summaries, " +
                "or use idsOnly=true for bill IDs, then request each bill from " +
                "/api/3/bills/{sessionYear}/{printNo}.",
                "Must be false or omitted on bill search and listing."),
        BILL_UPDATES_FULL_BILL("fullBill", "/static/docs/html/bills.html#bill-updates-fullbill-migration",
                "Bill update feeds no longer return full bills. Use summary=true for bill summaries, " +
                "or use the bill IDs in the update tokens to request each bill from " +
                "/api/3/bills/{sessionYear}/{printNo}.",
                "Must be false or omitted on bill update feeds.");

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
