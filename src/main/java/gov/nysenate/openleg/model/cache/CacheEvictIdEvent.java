package gov.nysenate.openleg.model.cache;

import com.google.common.collect.Sets;

public class CacheEvictIdEvent<ContentId> extends BaseCacheEvent {

    protected ContentId contentId;

    public CacheEvictIdEvent(ContentCache affectedCache, ContentId contentId) {
        super(Sets.newHashSet(affectedCache));
        this.contentId = contentId;
    }

    public ContentId getContentId() {
        return contentId;
    }
}
