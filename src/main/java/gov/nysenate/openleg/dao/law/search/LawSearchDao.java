package gov.nysenate.openleg.dao.law.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.rescore.RescoreBuilder;
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
    public SearchResults<LawDocId> searchLawDocs(QueryBuilder query, FilterBuilder filter, RescoreBuilder.Rescorer rescorer,
                                                 List<SortBuilder> sort, LimitOffset limOff);

    /**
     * Update the law index with the supplied law doc.
     *
     * @param lawDoc LawDocument
     */
    public void updateLawIndex(LawDocument lawDoc);

    /**
     * Update the law index with the supplied collection of law docs.
     *
     * @param lawDocs Collection<LawDocument>
     */
    public void updateLawIndex(Collection<LawDocument> lawDocs);

    /**
     * Removes the law document from the index.
     *
     * @param lawDocId LawDocId
     */
    public void deleteLawDocFromIndex(LawDocId lawDocId);
}
