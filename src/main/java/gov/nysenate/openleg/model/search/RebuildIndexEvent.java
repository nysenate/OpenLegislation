package gov.nysenate.openleg.model.search;

import gov.nysenate.openleg.dao.base.SearchIndex;

import java.util.Set;

public class RebuildIndexEvent extends BaseIndexEvent
{
    public RebuildIndexEvent(Set<SearchIndex> affectedIndices) {
        super(affectedIndices);
    }
}
