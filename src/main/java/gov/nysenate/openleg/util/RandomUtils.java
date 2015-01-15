package gov.nysenate.openleg.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class RandomUtils {

    private static SecureRandom secureRandom = new SecureRandom();

    public static String getRandomString(int numCharacters) {
        return new BigInteger(numCharacters*5, secureRandom).toString(32);
    }
}
