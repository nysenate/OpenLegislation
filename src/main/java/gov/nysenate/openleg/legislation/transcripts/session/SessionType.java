package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.common.util.NumberConversionUtils;
import org.apache.commons.text.WordUtils;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionType implements Comparable<SessionType> {
    private static final Pattern typePattern = Pattern.compile("(.*)(SESSION)(.*)");

    @Nonnull
    private final String typeString;
    private final int num;

    public SessionType(String sessionTypeStr) {
        Matcher matcher = typePattern.matcher(sessionTypeStr.toUpperCase().replaceAll("\\s+", ""));
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Missing 'SESSION' label in: " + sessionTypeStr);
        }
        if ("REGULAR".equals(matcher.group(1)) && matcher.group(3).isEmpty()) {
            this.num = -1;
        }
        else if ("EXTRAORDINARY".equals(matcher.group(1))) {
            try {
                this.num = NumberConversionUtils.numeralToInt(matcher.group(3));
            } catch (NullPointerException ignored) {
                throw new IllegalArgumentException("Cannot parse number from: " + sessionTypeStr);
            }
        }
        else {
            throw new IllegalArgumentException(sessionTypeStr + " does not match a known SessionType pattern.");
        }
        String capitals = WordUtils.capitalizeFully(matcher.group(1) + " " + matcher.group(2));
        this.typeString = capitals + (matcher.group(3).isEmpty() ? "" : " " + matcher.group(3));
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
        return num - o.num;
    }
}
