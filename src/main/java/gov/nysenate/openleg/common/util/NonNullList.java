package gov.nysenate.openleg.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A simple List implementation that ensures nulls are never in the list.
 */
public class NonNullList<E> {
    private final List<E> internalList = new ArrayList<>();

    /**
     * Note that nulls in the input are allowed, but ignored.
     */
    @SafeVarargs
    public static <E> NonNullList<E> of(E... elements) {
        var list = new NonNullList<E>();
        for (E element : elements) {
            list.addIfNotNull(element);
        }
        return list;
    }

    public void addIfNotNull(E element) {
        if (element != null) {
            internalList.add(element);
        }
    }

    public Stream<E> stream() {
        return internalList.stream();
    }
}
