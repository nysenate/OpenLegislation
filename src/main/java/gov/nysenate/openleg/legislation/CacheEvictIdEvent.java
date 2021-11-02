package gov.nysenate.openleg.legislation;

import com.google.common.collect.Sets;

public class CacheEvictIdEvent<ContentId> extends BaseCacheEvent {

    protected ContentId contentId;

    public CacheEvictIdEvent(CacheType affectedCache, ContentId contentId) {
        super(Sets.newHashSet(affectedCache));
        this.contentId = contentId;
    }

    public ContentId getContentId() {
        return contentId;
    }
}
