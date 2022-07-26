package gov.nysenate.openleg.search;

import java.util.Set;

public class BaseIndexEvent {
    private final Set<SearchIndex> affectedIndices;

    public BaseIndexEvent(Set<SearchIndex> affectedIndices) {
        this.affectedIndices = affectedIndices;
    }

    public boolean affects(SearchIndex index) {
        return affectedIndices.contains(index);
    }
}
