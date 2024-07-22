package gov.nysenate.openleg.common.util;

public class Tuple<T, R> {
    private final T v1;
    private final R v2;

    public Tuple(T v1, R v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public T v1() {
        return v1;
    }

    public R v2() {
        return v2;
    }
}
