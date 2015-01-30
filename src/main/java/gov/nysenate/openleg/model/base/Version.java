package gov.nysenate.openleg.model.base;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Enumeration of all possible character based versions which includes an entry for a default version.
 * Using this enumeration instead of a string will address issues pertaining to normalizing
 * the version strings and dealing with the default version which can be ambiguous in string form.
 */
public enum Version
{
    DEFAULT, // The default version
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    public String getValue() {
        return (this.equals(DEFAULT)) ? "" : this.name();
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
            return DEFAULT;
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
