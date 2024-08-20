package gov.nysenate.openleg.search.transcripts.session;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
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

import java.util.*;

@Service
public class ElasticTranscriptSearchService extends IndexedSearchService<Transcript> implements TranscriptSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticTranscriptSearchService.class);

    private final SearchDao<TranscriptId, TranscriptView, Transcript> transcriptSearchDao;
    private final TranscriptDataService transcriptDataService;

    @Autowired
    public ElasticTranscriptSearchService(SearchDao<TranscriptId, TranscriptView, Transcript> transcriptSearchDao,
                                          OpenLegEnvironment env, EventBus eventBus,
                                          TranscriptDataService transcriptDataService) {
        super(transcriptSearchDao, env);
        this.transcriptSearchDao = transcriptSearchDao;
        this.transcriptDataService = transcriptDataService;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<TranscriptId> searchTranscripts(String queryStr, Integer year, String sort, LimitOffset limOff)
            throws SearchException {
        return transcriptSearchDao.searchForIds(getYearRangeQuery("dateTime", year), queryStr, sort, limOff);
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
    public void rebuildIndex() {
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
    }
}
