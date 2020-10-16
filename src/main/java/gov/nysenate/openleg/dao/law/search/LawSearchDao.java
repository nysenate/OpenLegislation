package gov.nysenate.openleg.dao.law.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

/**
 * DAO Interface for searching law data.
 */
public interface LawSearchDao
{
    /**
     * Perform a search against all law documents.
     *
     * @param query QueryBuilder
     * @param filter FilterBuilder
     * @param rescorer Rescorer
     * @param sort String
     * @param limOff LimitOffset
     * @return SearchResults<LawDocId>
     */
    SearchResults<LawDocId> searchLawDocs(QueryBuilder query, QueryBuilder filter, RescorerBuilder<?> rescorer,
                                          List<SortBuilder<?>> sort, LimitOffset limOff);

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
