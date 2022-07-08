package gov.nysenate.openleg.legislation.bill;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Enumeration of all possible character based versions which includes an entry for a default version.
 * Using this enumeration instead of a string will address issues pertaining to normalizing
 * the version strings and dealing with the default version which can be ambiguous in string form.
 */
public enum Version
{
    ORIGINAL, // The original version
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    /**
     * For use when displaying a bill's Version.
     */
    @Override
    public String toString(){
        return (this == ORIGINAL) ? "" : this.name();
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
        if (cleanVersion.isEmpty() || cleanVersion.equals("DEFAULT")) {
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
        return Arrays.stream(values()).filter(p -> p.compareTo(v) < 0).toList();
    }

    /**
     * Get a list containing the versions that occur after the given version 'v'.
     *
     * @param v Version
     * @return List<Version>
     */
    public static List<Version> after(Version v) {
        return Arrays.stream(values()).filter(p -> p.compareTo(v) > 0).toList();
    }

    /**
     * A class to compare Versions once they are already made into strings.
     */
    public static class SortVersionStrings implements Comparator<String>, Serializable
    {
        public int compare(String str1, String str2){
            Version v1 = of(str1);
            Version v2 = of(str2);
            return v1.compareTo(v2);
        }
    }

}
