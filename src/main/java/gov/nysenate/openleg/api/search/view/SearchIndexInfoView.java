package gov.nysenate.openleg.api.search.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.search.SearchIndex;

public record SearchIndexInfoView(String name, String indexName, boolean primaryStore) implements ViewObject {

    public SearchIndexInfoView(SearchIndex searchIndex) {
        this(searchIndex.name(), searchIndex.getName(), searchIndex.isPrimaryStore());
    }

    @Override
    public String getViewType() {
        return "search-index-info";
    }
}
