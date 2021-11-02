package gov.nysenate.openleg.legislation;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseCacheEvent
{
    protected Set<CacheType> affectedCaches = new HashSet<>();

    protected BaseCacheEvent(Set<CacheType> affectedCaches) {
        this.affectedCaches = affectedCaches;
    }

    public boolean affects(CacheType cacheType) {
        return affectedCaches.contains(cacheType);
    }

    public Set<CacheType> getAffectedCaches() {
        return affectedCaches;
    }
}
