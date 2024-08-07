package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;

import java.util.Collection;
import java.util.List;

public interface SearchDao<IdType, ViewType, ContentType> {
    void createIndices();

    void purgeIndices();

    SearchResults<IdType> searchForIds(Query query, List<SortOptions> sort, LimitOffset limOff);

    SearchResults<ViewType> searchForDocs(Query query, List<SortOptions> sort, LimitOffset limOff);

    void updateIndex(ContentType data);

    void updateIndex(Collection<ContentType> data);

    void deleteFromIndex(IdType id);
}
