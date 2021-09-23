package gov.nysenate.openleg.search;

import java.util.Set;

public class BaseIndexEvent
{
    protected Set<SearchIndex> affectedIndices;

    public BaseIndexEvent(Set<SearchIndex> affectedIndices) {
        this.affectedIndices = affectedIndices;
    }

    public boolean affects(SearchIndex index) {
        return affectedIndices.contains(index);
    }

    public Set<SearchIndex> getAffectedIndices() {
        return affectedIndices;
    }
}
