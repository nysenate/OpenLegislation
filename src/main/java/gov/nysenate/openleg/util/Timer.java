package gov.nysenate.openleg.util;

public class Timer {
    Long s = null;
    public void start() {
        s = System.currentTimeMillis();
    }
    public double stop() {
        if(s == null)
            return -1L;

        long e = System.currentTimeMillis() - s;

        s = null;
        return e/1000.0;
    }
}
