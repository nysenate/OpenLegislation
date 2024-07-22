package gov.nysenate.openleg.search.law;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.Rescore;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.search.SearchResults;

import java.util.Collection;
import java.util.List;

/**
 * DAO Interface for searching law data.
 */
public interface LawSearchDao {
    /**
     * Perform a search against all law documents.
     */
    SearchResults<LawDocId> searchLawDocs(Query query, Query postFilter,
                                          Rescore rescorer, List<SortOptions> sort, LimitOffset limOff);

    /**
     * Update the law index with the supplied law doc.
     *
     * @param lawDoc LawDocument
     */
    void updateLawIndex(LawDocument lawDoc);

    /**
     * Update the law index with the supplied collection of law docs.
     *
     * @param lawDocs Collection<LawDocument>
     */
    void updateLawIndex(Collection<LawDocument> lawDocs);

    /**
     * Removes the given law documents from the index
     *
     * @param lawDocIds {@link Collection<LawDocId>}
     */
    void deleteLawDocsFromIndex(Collection<LawDocId> lawDocIds);
}
