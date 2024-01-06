package gov.nysenate.openleg.search;

import java.util.Set;

public class ClearIndexEvent extends BaseIndexEvent {
    public ClearIndexEvent(Set<SearchIndex> affectedIndices) {
        super(affectedIndices);
    }
}
