package gov.nysenate.openleg.search.transcripts.session;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ElasticTranscriptSearchService implements TranscriptSearchService, IndexedSearchService<Transcript> {
    private static final Logger logger = LoggerFactory.getLogger(ElasticTranscriptSearchService.class);

    private final OpenLegEnvironment env;
    private final ElasticTranscriptSearchDao transcriptSearchDao;
    private final TranscriptDataService transcriptDataService;

    @Autowired
    public ElasticTranscriptSearchService(OpenLegEnvironment env,
                                          ElasticTranscriptSearchDao transcriptSearchDao,
                                          TranscriptDataService transcriptDataService,
                                          EventBus eventBus) {
        this.env = env;
        this.transcriptSearchDao = transcriptSearchDao;
        this.transcriptDataService = transcriptDataService;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<TranscriptId> searchTranscripts(String query, Integer year, String sort, LimitOffset limOff)
            throws SearchException {
        return search(IndexedSearchService.getStringQuery(query), year, sort, limOff);
    }

    private SearchResults<TranscriptId> search(Query query, Integer year,
                                               String sort, LimitOffset limOff) throws SearchException {
        if (limOff == null) {
            limOff = LimitOffset.TEN;
        }
        if (year != null) {
            var rangeQuery = RangeQuery.of(b -> b.field("dateTime")
                    .from(LocalDate.of(year, 1, 1).toString())
                    .to(LocalDate.of(year, 12, 31).toString()));
            final Query finalQuery = query;
            query = BoolQuery.of(b -> b.must(finalQuery, rangeQuery._toQuery()))._toQuery();
        }
        return transcriptSearchDao.searchTranscripts(query, null,
                ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleTranscriptUpdate(TranscriptUpdateEvent transcriptUpdateEvent) {
        if (transcriptUpdateEvent.transcript() != null) {
            updateIndex(transcriptUpdateEvent.transcript());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Transcript transcript) {
        if (env.isElasticIndexing() && transcript != null) {
            logger.info("Indexing transcript {} into elastic search.", transcript.getDateTime().toString());
            transcriptSearchDao.updateTranscriptIndex(transcript);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Transcript> transcripts) {
        if (env.isElasticIndexing() && !transcripts.isEmpty()) {
            List<Transcript> indexableTranscripts = transcripts.stream().filter(Objects::nonNull).toList();
            logger.info("Indexing {} valid transcripts into elasticsearch.", indexableTranscripts.size());
            transcriptSearchDao.updateTranscriptIndex(indexableTranscripts);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        transcriptSearchDao.purgeIndices();
        transcriptSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        final int bulkSize = 500;
        Queue<TranscriptId> transcriptIdQueue =
                new ArrayDeque<>(transcriptDataService.getTranscriptIds(SortOrder.DESC, LimitOffset.ALL));
        while(!transcriptIdQueue.isEmpty()) {
            List<Transcript> transcripts = new ArrayList<>(bulkSize);
            for (int i = 0; i < bulkSize && !transcriptIdQueue.isEmpty(); i++) {
                TranscriptId tid = transcriptIdQueue.remove();
                transcripts.add(transcriptDataService.getTranscript(tid));
            }
            updateIndex(transcripts);
        }
        logger.info("Finished reindexing transcripts.");
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.TRANSCRIPT)) {
            logger.info("Handling transcript re-index event.");
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.TRANSCRIPT)) {
            clearIndex();
        }
    }
}
