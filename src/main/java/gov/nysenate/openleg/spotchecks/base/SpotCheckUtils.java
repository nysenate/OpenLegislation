package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatch;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of methods useful for spotchecks.
 */
@Service
public class SpotCheckUtils {
    private final MemberService memberService;

    @Autowired
    public SpotCheckUtils(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Check two string values, generating a mismatch of the given type if one is found.
     *
     * @param content String
     * @param reference String
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     */
    public void checkString(String content, String reference,
                               SpotCheckObservation<?> observation, SpotCheckMismatchType mismatchType) {
        // Ensure that the mismatch can be reported in the observation.
        observation.checkReportable(mismatchType);

        content = StringUtils.trimToEmpty(content);
        reference = StringUtils.trimToEmpty(reference);
        if (!StringUtils.equals(content, reference)) {
            observation.addMismatch(new SpotCheckMismatch(mismatchType, content, reference));
        }
    }

    /**
     * Check two string values case insensitively after uppercase conversion.
     *
     * @see #checkString(String, String, SpotCheckObservation, SpotCheckMismatchType)
     * @param content String
     * @param reference String
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     */
    public void checkStringUpper(String content, String reference,
                                    SpotCheckObservation<?> observation, SpotCheckMismatchType mismatchType) {
        checkString(StringUtils.upperCase(content), StringUtils.upperCase(reference), observation, mismatchType);
    }

    /**
     * Check two objects by converting them into strings with the given function.
     *
     * @see #checkString(String, String, SpotCheckObservation, SpotCheckMismatchType)
     * @param content T
     * @param reference T
     * @param toStringFn Function<T, String> function to convert given objects to string for checking
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     */
    public <T> void checkObject(T content, T reference, Function<T, String> toStringFn,
                                SpotCheckObservation<?> observation, SpotCheckMismatchType mismatchType) {
        checkString(toStringFn.apply(content), toStringFn.apply(reference), observation, mismatchType);
    }

    /**
     * Check two objects by converting them into strings using their toString implementations.
     *
     * @see #checkObject(Object, Object, Function, SpotCheckObservation, SpotCheckMismatchType)
     * @param content Object
     * @param reference Object
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     */
    public void checkObject(Object content, Object reference,
                               SpotCheckObservation<?> observation, SpotCheckMismatchType mismatchType) {
        checkObject(content, reference, Objects::toString, observation, mismatchType);
    }

    /**
     * Check two boolean values by converting them into strings.
     *
     * @see #checkString(String, String, SpotCheckObservation, SpotCheckMismatchType)
     * @param content boolean
     * @param reference boolean
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     */
    public void checkBoolean(boolean content, boolean reference, String condition,
                                SpotCheckObservation<?> observation,
                                SpotCheckMismatchType mismatchType) {
        checkString(getBooleanString(content, condition),
                getBooleanString(reference, condition),
                observation, mismatchType);
    }

    /**
     * Check two collections by converting them into strings.
     *
     * @see #checkString(String, String, SpotCheckObservation, SpotCheckMismatchType)
     * @param content String
     * @param reference String
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     * @param toString Function used to convert each item in each collection to a String
     * @param split String used as a delimiter for each item
     * @param sort boolean if true, the string values of each collections' items will be sorted before concatenation.
     */
    public <T> void checkCollection(Collection<T> content, Collection<T> reference,
                                       SpotCheckObservation<?> observation,
                                       SpotCheckMismatchType mismatchType,
                                       Function<? super T, ? extends CharSequence> toString,
                                       String split,
                                       boolean sort) {
        String contentStr = stringifyCollection(content, toString, split, sort);
        String refStr = stringifyCollection(reference, toString, split, sort);
        checkString(contentStr, refStr, observation, mismatchType);
    }

    /**
     * Check two collections by converting them into strings.
     *
     * This overload does not include a sort option, defaulting to false.
     * @see #checkCollection(Collection, Collection, SpotCheckObservation, SpotCheckMismatchType, Function, String)
     * @param content String
     * @param reference String
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     * @param toString Function used to convert each item in each collection to a string
     * @param split String used as a delimiter for each item
     */
    public <T> void checkCollection(Collection<T> content, Collection<T> reference,
                                       SpotCheckObservation<?> observation,
                                       SpotCheckMismatchType mismatchType,
                                       Function<? super T, ? extends CharSequence> toString,
                                       String split) {
        checkCollection(content, reference, observation, mismatchType, toString, split, false);
    }

    /**
     * Check two collections by converting them into strings.
     *
     * This overload converts each collection by converting each item with its toString and delimiting with spaces.
     * @see #checkCollection(Collection, Collection, SpotCheckObservation, SpotCheckMismatchType, Function, String)
     * @param content String
     * @param reference String
     * @param observation {@link SpotCheckObservation}
     * @param mismatchType {@link SpotCheckMismatchType}
     */
    public <T> void checkCollection(Collection<T> content, Collection<T> reference,
                                       SpotCheckObservation<?> observation,
                                       SpotCheckMismatchType mismatchType) {
        checkCollection(content, reference, observation, mismatchType, Objects::toString, " ");
    }

    /**
     * Given a shortname and session, return the primary shortname for that member/session
     */
    public String getPrimaryShortname(SessionYear sessionYear, Chamber chamber, String shortname) {
        if (StringUtils.isBlank(shortname)) {
            return null;
        }
        try {
            SessionMember sessionMember = memberService.getSessionMemberByShortName(shortname, sessionYear, chamber);
            return getPrimaryShortname(sessionYear, sessionMember.getMember().getMemberId());
        } catch (MemberNotFoundEx ex) {
            return "<unknown shortname: " + sessionYear + " " + chamber + " " + shortname + ">";
        }
    }

    /**
     * Get the primary shortname of the given member for the given session
     */
    public String getPrimaryShortname(SessionYear sessionYear, int memberId) {
        try {
            SessionMember sessionMember = memberService.getSessionMemberById(memberId, sessionYear);
            return sessionMember.getLbdcShortName();
        } catch (MemberNotFoundEx ex) {
            return "<invalid session/memberId: " + sessionYear + "/" + memberId + ">";
        }
    }


    /* --- Internal Methods --- */

    private static String getBooleanString(boolean value, String condition) {
        return condition + ": " + (value ? "YES" : "NO");
    }

    private static <T> String stringifyCollection(Collection<T> collection,
                                           Function<? super T, ? extends CharSequence> toString,
                                           String split,
                                           boolean sort) {
        Stream<String> s = Optional.ofNullable(collection)
                .orElse(Collections.emptyList()).stream()
                .map(toString)
                .map(CharSequence::toString);

        if (sort) {
            s = s.sorted();
        }

        return s.collect(Collectors.joining(split));
    }

}
