package gov.nysenate.openleg.config;

public interface CacheTester {
    String methodReturnsMoose();
    String methodReturnsArg(String s);
    default String methodReturnsArgNotCached(String s) {
        return s;
    }
    void clear();
}
