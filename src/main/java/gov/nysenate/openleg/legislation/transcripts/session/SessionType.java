package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.common.util.NumberConversionUtils;
import org.apache.commons.text.WordUtils;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionType implements Comparable<SessionType> {
    private static final Pattern typePattern = Pattern.compile("(.*)(SESSION)(.*)");
    private static final String regular = "Regular";

    @Nonnull
    private final String typeString;

    public SessionType(String sessionTypeStr) {
        Matcher matcher = typePattern.matcher(sessionTypeStr.toUpperCase().replaceAll("\\s+", ""));
        if (!matcher.matches() || !validate(matcher)) {
            throw new IllegalArgumentException("Cannot parse session label: " + sessionTypeStr);
        }
        String capitals = WordUtils.capitalizeFully(matcher.group(1) + " " + matcher.group(2));
        this.typeString = capitals + (matcher.group(3).isEmpty() ? "" : " " + matcher.group(3));
    }

    private static boolean validate(Matcher matcher) {
        if (regular.toUpperCase().equals(matcher.group(1)) && matcher.group(3).isEmpty()) {
            return true;
        }
        else if ("EXTRAORDINARY".equals(matcher.group(1))) {
            try {
                NumberConversionUtils.numeralToInt(matcher.group(3));
                return true;
            } catch (NullPointerException ignored) {}
        }
        return false;
    }

    @Override
    public String toString() {
        return typeString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionType that = (SessionType) o;
        return compareTo(that) == 0;
    }

    @Override
    public int compareTo(@Nonnull SessionType o) {
        if (typeString.startsWith(regular) || o.typeString.startsWith(regular)) {
            return o.typeString.compareTo(this.typeString);
        }
        return typeString.compareTo(o.typeString);
    }
}
