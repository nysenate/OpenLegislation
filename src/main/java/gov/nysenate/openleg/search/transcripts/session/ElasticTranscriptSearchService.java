package gov.nysenate.openleg.search.transcripts.session;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ElasticTranscriptSearchService extends IndexedSearchService<Transcript>
        implements TranscriptSearchService {
    private final SearchDao<TranscriptId, TranscriptView, Transcript> transcriptSearchDao;
    private final TranscriptDataService transcriptDataService;

    @Autowired
    public ElasticTranscriptSearchService(SearchDao<TranscriptId, TranscriptView, Transcript> transcriptSearchDao,
                                          TranscriptDataService transcriptDataService, EventBus eventBus) {
        super(transcriptSearchDao);
        this.transcriptSearchDao = transcriptSearchDao;
        this.transcriptDataService = transcriptDataService;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<TranscriptId> searchTranscripts(String queryStr, Integer year, String sort, LimitOffset limOff,
                                                         boolean sessionOnly) throws SearchException {
        QueryVariant dayTypeQuery = null;
        if (sessionOnly) {
            dayTypeQuery = MatchQuery.of(b -> b.field("dayType").query(DayType.SESSION.toString()));
        }
        return transcriptSearchDao.searchForIds(queryStr, sort, limOff, getYearRangeQuery("dateTime", year), dayTypeQuery);
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
