package gov.nysenate.openleg.search;

import gov.nysenate.openleg.model.ISenateObject;

import java.util.Iterator;

/**
 * An easier, iterable way to move through multiple result sets
 *
 * @param <T>
 */
public class SenateObjectSearch<T extends ISenateObject> implements Iterator<T>, Iterable<T> {
    private static final int SIZE = 500;
    private static final int PAGE = 0;
    private static final String FORMAT = "json";
    private static final String SORT_BY = "oid";
    private static final boolean REVERSE = false;

    ResultSearch resultSearch;

    public Class<T> clazz;

    public SenateObjectSearch() {
        this(SIZE, PAGE, FORMAT, SORT_BY, REVERSE);
    }

    public SenateObjectSearch(int max, int page, String format, String sortBy, boolean reverse) {
        resultSearch = new ResultSearch(max, page, format, sortBy, reverse);
    }

    public SenateObjectSearch<T> query(String query) {
        resultSearch.query(query);
        return this;
    }

    public SenateObjectSearch<T> clazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public boolean hasNext() {
        return resultSearch.hasNext();
    }

    @Override
    public T next() {
        return result(resultSearch.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private T result(Result result) {
        return (T) result.getObject();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
