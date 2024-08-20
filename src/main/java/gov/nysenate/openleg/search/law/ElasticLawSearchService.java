package gov.nysenate.openleg.search.law;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.legislation.law.LawInfo;
import gov.nysenate.openleg.legislation.law.dao.LawDataDao;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.law.BulkLawUpdateEvent;
import gov.nysenate.openleg.updates.law.LawTreeUpdateEvent;
import gov.nysenate.openleg.updates.law.LawUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class ElasticLawSearchService extends IndexedSearchService<LawDocument> implements LawSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticLawSearchService.class);

    private final ElasticLawSearchDao lawSearchDao;
    private final LawDataDao lawDataDao;
    private final LawDataService lawDataService;

    @Autowired
    public ElasticLawSearchService(ElasticLawSearchDao lawSearchDao, OpenLegEnvironment env,
                                   EventBus eventBus, LawDataDao lawDataDao,
                                   LawDataService lawDataService) {
        super(lawSearchDao, env);
        this.lawSearchDao = lawSearchDao;
        this.lawDataDao = lawDataDao;
        this.lawDataService = lawDataService;
        eventBus.register(this);
    }

    /* --- LawSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(String queryStr, String lawId, String sort, LimitOffset limOff) throws SearchException {
        var lawIdQuery = lawId == null ? null : MatchQuery.of(b -> b.field("lawId").query(lawId));
        return lawSearchDao.searchForIds(lawIdQuery, queryStr, sort, limOff);
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

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<LawDocument> content) {
        super.updateIndex(content.stream()
                .filter(this::isLawDocIndexable)
                .toList());
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        lawDataDao.getLawInfos().stream()
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
     * Determines if a law document can be indexed.
     * The document must be present in the current law tree.
     */
    private boolean isLawDocIndexable(LawDocument doc) {
        try {
            return lawDataService.getLawTree(doc.getLawId())
                    .find(doc.getDocumentId()).isPresent();
        }
        catch (LawTreeNotFoundEx e) {
            return false;
        }
    }
}