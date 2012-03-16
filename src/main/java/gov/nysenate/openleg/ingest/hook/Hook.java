package gov.nysenate.openleg.ingest.hook;

public interface Hook<T> {
    public void call(T t);
}