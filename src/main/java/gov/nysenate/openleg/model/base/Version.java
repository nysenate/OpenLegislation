package gov.nysenate.openleg.model.base;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Enumeration of all possible character based versions which includes an entry for a default version.
 * Using this enumeration instead of a string will address issues pertaining to normalizing
 * the version strings and dealing with the default version which can be ambiguous in string form.
 */
public enum Version
{
    ORIGINAL, // The original version
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    public final static String DEFAULT_VERSION_NAME = "ORIGINAL";

    public String getValue() {
        return this == ORIGINAL ?  DEFAULT_VERSION_NAME : this.name();
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    /**
     * Given an input string return the matching Version. Use this method instead of
     * valueOf() as it will handle the default version as well as normalize the input.
     * An IllegalArgumentException will be thrown if the given version is invalid.
     *
     * @param version String
     * @return Version
     */
    public static Version of(String version) {
        String cleanVersion = (version != null) ? version.trim().toUpperCase() : "";
        if (cleanVersion.isEmpty() || cleanVersion.equals("DEFAULT") || cleanVersion.equals("ORIGINAL")) {
            return ORIGINAL;
        }
        return valueOf(cleanVersion);
    }

    /**
     * Get a list containing the versions that occur before the given version 'v'.
     *
     * @param v Version
     * @return List<Version>
     */
    public static List<Version> before(Version v) {
        return Arrays.stream(values()).filter(p -> p.compareTo(v) < 0).collect(toList());
    }

    /**
     * Get a list containing the versions that occur after the given version 'v'.
     *
     * @param v Version
     * @return List<Version>
     */
    public static List<Version> after(Version v) {
        return Arrays.stream(values()).filter(p -> p.compareTo(v) > 0).collect(toList());
    }
}
