package gov.nysenate.openleg.model.cache;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseCacheEvent
{
    protected Set<ContentCache> affectedCaches = new HashSet<>();

    protected BaseCacheEvent(Set<ContentCache> affectedCaches) {
        this.affectedCaches = affectedCaches;
    }

    public boolean affects(ContentCache contentCache) {
        return affectedCaches.contains(contentCache);
    }

    public Set<ContentCache> getAffectedCaches() {
        return affectedCaches;
    }
}
