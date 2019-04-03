package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseSpotCheckService<ContentKey, ContentType, ReferenceType>
        implements SpotCheckService<ContentKey, ContentType, ReferenceType> {

    @Autowired
    private MemberService memberService;

    protected void checkString(String content, String reference,
                               SpotCheckObservation<ContentKey> observation, SpotCheckMismatchType mismatchType) {
        // Ensure that the mismatch can be reported in the observation.
        observation.checkReportable(mismatchType);

        content = StringUtils.trimToEmpty(content);
        reference = StringUtils.trimToEmpty(reference);
        if (!StringUtils.equals(content, reference)) {
            observation.addMismatch(new SpotCheckMismatch(mismatchType, content, reference));
        }
    }

    protected void checkStringUpper(String content, String reference,
                                    SpotCheckObservation<ContentKey> observation, SpotCheckMismatchType mismatchType) {
        checkString(StringUtils.upperCase(content), StringUtils.upperCase(reference), observation, mismatchType);
    }

    protected void checkObject(Object content, Object reference,
                               SpotCheckObservation<ContentKey> observation, SpotCheckMismatchType mismatchType) {
        checkString(Objects.toString(content), Objects.toString(reference), observation, mismatchType);
    }

    protected void checkBoolean(boolean content, boolean reference, String condition,
                                SpotCheckObservation<ContentKey> observation,
                                SpotCheckMismatchType mismatchType) {
        checkString(getBooleanString(content, condition),
                getBooleanString(reference, condition),
                observation, mismatchType);
    }

    protected <T> void checkCollection(Collection<T> content, Collection<T> reference,
                                       SpotCheckObservation<ContentKey> observation,
                                       SpotCheckMismatchType mismatchType,
                                       Function<? super T, ? extends CharSequence> toString,
                                       String split,
                                       boolean sort) {
        String contentStr = stringifyCollection(content, toString, split, sort);
        String refStr = stringifyCollection(reference, toString, split, sort);
        checkString(contentStr, refStr, observation, mismatchType);
    }

    protected <T> void checkCollection(Collection<T> content, Collection<T> reference,
                                       SpotCheckObservation<ContentKey> observation,
                                       SpotCheckMismatchType mismatchType,
                                       Function<? super T, ? extends CharSequence> toString,
                                       String split) {
        checkCollection(content, reference, observation, mismatchType, toString, split, false);
    }

    protected <T> void checkCollection(Collection<T> content, Collection<T> reference,
                                       SpotCheckObservation<ContentKey> observation,
                                       SpotCheckMismatchType mismatchType) {
        checkCollection(content, reference, observation, mismatchType, Objects::toString, " ");
    }

    /**
     * Given a shortname and session, return the primary shortname for that member/session
     */
    protected String getPrimaryShortname(SessionYear sessionYear, Chamber chamber, String shortname) {
        if (StringUtils.isBlank(shortname)) {
            return null;
        }
        try {
            SessionMember member = memberService.getMemberByShortName(shortname, sessionYear, chamber);
            return getPrimaryShortname(sessionYear, member.getMemberId());
        } catch (MemberNotFoundEx ex) {
            return "<unknown shortname: " + sessionYear + " " + chamber + " " + shortname + ">";
        }
    }

    /**
     * Get the primary shortname of the given member for the given session
     */
    protected String getPrimaryShortname(SessionYear sessionYear, int memberId) {
        try {
            SessionMember sessionMember = memberService.getMemberById(memberId, sessionYear);
            return sessionMember.getLbdcShortName();
        } catch (MemberNotFoundEx ex) {
            return "<invalid session/memberId: " + sessionYear + "/" + memberId + ">";
        }
    }


    /* --- Internal Methods --- */

    private String getBooleanString(boolean value, String condition) {
        return condition + ": " + (value ? "YES" : "NO");
    }

    private <T> String stringifyCollection(Collection<T> collection,
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
