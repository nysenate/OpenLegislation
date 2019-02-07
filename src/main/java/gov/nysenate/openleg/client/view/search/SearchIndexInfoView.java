package gov.nysenate.openleg.client.view.search;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.dao.base.SearchIndex;

public class SearchIndexInfoView implements ViewObject {

    private String name;
    private String indexName;
    private boolean primaryStore;

    public SearchIndexInfoView(SearchIndex searchIndex) {
        this.name = searchIndex.name();
        this.indexName = searchIndex.getIndexName();
        this.primaryStore = searchIndex.isPrimaryStore();
    }

    @Override
    public String getViewType() {
        return "search-index-info";
    }

    public String getName() {
        return name;
    }

    public String getIndexName() {
        return indexName;
    }

    public boolean isPrimaryStore() {
        return primaryStore;
    }
}
