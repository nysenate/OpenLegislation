package gov.nysenate.openleg.model.cache;

import java.util.Set;

public class CacheWarmEvent extends BaseCacheEvent
{
    public CacheWarmEvent(Set<ContentCache> affectedCaches) {
        super(affectedCaches);
    }
}