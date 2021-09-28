package testing_utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    // Used to round off publishedDateTimes, like Postgres 12 does.
    public static LocalDateTime roundToMicroseconds(LocalDateTime ldt) {
        long fixedNanos = 1000 * Math.round((double)ldt.getNano()/1000);
        return ldt.withNano((int) fixedNanos);
    }
}
