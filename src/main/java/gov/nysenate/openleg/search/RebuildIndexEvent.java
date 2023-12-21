package gov.nysenate.openleg.search;

import java.util.Set;

public class RebuildIndexEvent extends BaseIndexEvent {
    public RebuildIndexEvent(Set<SearchIndex> affectedIndices) {
        super(affectedIndices);
    }
}
