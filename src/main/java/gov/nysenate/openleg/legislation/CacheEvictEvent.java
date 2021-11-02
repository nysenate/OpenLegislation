package gov.nysenate.openleg.legislation;

import java.util.Set;

public class CacheEvictEvent extends BaseCacheEvent
{
    public CacheEvictEvent(Set<CacheType> affectedCaches) {
        super(affectedCaches);
    }
}
