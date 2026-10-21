package gov.nysenate.openleg.search.law;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawInfo;
import gov.nysenate.openleg.legislation.law.LawTree;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.law.BulkLawUpdateEvent;
import gov.nysenate.openleg.updates.law.LawTreeUpdateEvent;
import gov.nysenate.openleg.updates.law.LawUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ElasticLawSearchService extends IndexedSearchService<LawDocument> implements LawSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticLawSearchService.class);

    private final ElasticLawSearchDao lawSearchDao;
    private final LawDataService lawDataService;

    @Autowired
    public ElasticLawSearchService(ElasticLawSearchDao lawSearchDao, LawDataService lawDataService,
                                   @Qualifier("eventBus") EventBus eventBus) {
        super(lawSearchDao);
        this.lawSearchDao = lawSearchDao;
        this.lawDataService = lawDataService;
        eventBus.register(this);
    }

    /* --- LawSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(String queryStr, String lawId, String sort, LimitOffset limOff) throws SearchException {
        var lawIdQuery = lawId == null ? null : MatchQuery.of(b -> b.field("lawId").query(lawId));
        return lawSearchDao.searchForIds(queryStr, sort, limOff, lawIdQuery);
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleLawUpdate(LawUpdateEvent lawUpdateEvent) {
        if (lawUpdateEvent != null && lawUpdateEvent.lawDoc() != null) {
            updateIndex(lawUpdateEvent.lawDoc());
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleBulkLawUpdate(BulkLawUpdateEvent bulkLawUpdateEvent) {
        if (bulkLawUpdateEvent != null && !bulkLawUpdateEvent.lawDocuments().isEmpty()) {
            updateIndex(bulkLawUpdateEvent.lawDocuments());
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleLawTreeUpdate(LawTreeUpdateEvent lawTreeUpdateEvent) {
        String lawId = lawTreeUpdateEvent.lawChapterId();
        logger.info("Clearing law chapter {} from index", lawId);
        try {
            SearchResults<LawDocId> chapterDocs = searchLawDocs(null, lawId, null, LimitOffset.ALL);
            lawSearchDao.deleteLawDocsFromIndex(chapterDocs.getRawResults());
        } catch (SearchException ignored) {}
        indexLawChapter(lawId);
    }

    /**
     * Indexes the documents that are in their chapter's current law tree.
     * Each chapter's tree is loaded once for the whole collection: it can be large,
     * and loading it for every document makes indexing a chapter quadratic.
     */
    @Override
    public void updateIndex(Collection<LawDocument> content) {
        Map<String, Optional<LawTree>> currentTrees = content.stream()
                .map(LawDocument::getLawId)
                .distinct()
                .collect(Collectors.toMap(Function.identity(), this::getCurrentLawTree));
        super.updateIndex(content.stream()
                .filter(doc -> currentTrees.get(doc.getLawId())
                        .flatMap(tree -> tree.find(doc.getDocumentId()))
                        .isPresent())
                .toList());
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        lawDataService.getLawInfos().stream()
                .map(LawInfo::getLawId)
                .sorted()
                .forEach(this::indexLawChapter);
    }

    /**
     * Indexes all published documents in a law chapter.
     * Does not clear the chapter, so previously indexed documents may still be present.
     * @param lawId String - law chapter id
     */
    private void indexLawChapter(String lawId) {
        logger.info("Indexing law chapter {}", lawId);
        Collection<LawDocument> lawDocs = lawDataService.getLawDocuments(lawId, LocalDate.now()).values();
        updateIndex(lawDocs);
    }

    /**
     * Gets the current law tree of a chapter, or empty if the chapter has none.
     * Documents can only be indexed if they are present in this tree.
     */
    private Optional<LawTree> getCurrentLawTree(String lawId) {
        try {
            return Optional.of(lawDataService.getLawTree(lawId));
        }
        catch (LawTreeNotFoundEx e) {
            return Optional.empty();
        }
    }
}