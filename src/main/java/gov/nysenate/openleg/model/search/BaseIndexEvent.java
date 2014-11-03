package gov.nysenate.openleg.model.search;

import gov.nysenate.openleg.dao.base.SearchIndex;

import java.util.HashSet;
import java.util.Set;

public class BaseIndexEvent
{
    protected Set<SearchIndex> affectedIndices = new HashSet<>();

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
