package gov.nysenate.openleg.common.util;

import org.elasticsearch.common.collect.Tuple;

public class Pair<T> extends Tuple<T, T> {
    public Pair(T t, T t2) {
        super(t, t2);
    }
}
