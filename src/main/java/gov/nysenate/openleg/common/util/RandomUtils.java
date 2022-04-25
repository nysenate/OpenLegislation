package gov.nysenate.openleg.common.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class RandomUtils {
    private static final SecureRandom secureRandom = new SecureRandom();

    private RandomUtils() {}

    public static String getRandomString(int numCharacters) {
        return new BigInteger(numCharacters*5, secureRandom).toString(32);
    }
}
