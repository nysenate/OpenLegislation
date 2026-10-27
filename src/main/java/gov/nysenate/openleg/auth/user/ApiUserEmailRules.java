package gov.nysenate.openleg.auth.user;

import jakarta.annotation.Nullable;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Which email addresses API users can have. Signup and batch email hold addresses to this one rule, so an
 * address signup accepts can be sent batch email.
 */
public final class ApiUserEmailRules {
    private ApiUserEmailRules() {}

    private static final EmailValidator VALIDATOR = EmailValidator.getInstance();

    /** Whether an address is well formed and has a known top-level domain. A + and an apostrophe are allowed. */
    public static boolean isValid(@Nullable String email) {
        return VALIDATOR.isValid(email);
    }
}
