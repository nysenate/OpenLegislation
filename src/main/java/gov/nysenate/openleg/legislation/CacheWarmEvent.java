package gov.nysenate.openleg.legislation;

import java.util.Set;

public class CacheWarmEvent extends BaseCacheEvent
{
    public CacheWarmEvent(Set<CacheType> affectedCaches) {
        super(affectedCaches);
    }
}