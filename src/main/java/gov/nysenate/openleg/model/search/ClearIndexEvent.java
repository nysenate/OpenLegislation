package gov.nysenate.openleg.model.search;

import gov.nysenate.openleg.dao.base.SearchIndex;

import java.util.Set;

public class ClearIndexEvent extends BaseIndexEvent
{
    public ClearIndexEvent(Set<SearchIndex> affectedIndices) {
        super(affectedIndices);
    }
}
