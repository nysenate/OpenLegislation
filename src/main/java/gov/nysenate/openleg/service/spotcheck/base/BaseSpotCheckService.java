package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseSpotCheckService<ContentKey, ContentType, ReferenceType>
        implements SpotCheckService<ContentKey, ContentType, ReferenceType> {

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
