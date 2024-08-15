package gov.nysenate.openleg.search.transcripts.hearing;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ElasticHearingSearchService extends IndexedSearchService<Hearing> implements HearingSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticHearingSearchService.class);

    private final SearchDao<HearingId, HearingView, Hearing> hearingSearchDao;
    private final HearingDataService hearingDataService;

    @Autowired
    public ElasticHearingSearchService(SearchDao<HearingId, HearingView, Hearing> hearingSearchDao,
                                       OpenLegEnvironment env, EventBus eventBus,
                                       HearingDataService hearingDataService) {
        super(hearingSearchDao, env);
        this.hearingSearchDao = hearingSearchDao;
        this.hearingDataService = hearingDataService;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<HearingId> searchHearings(String queryStr, Integer year, String sort, LimitOffset limOff) throws SearchException {
        return hearingSearchDao.searchForIds(getYearRangeQuery("date", year), queryStr, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleHearingUpdate(HearingUpdateEvent hearingUpdateEvent) {
        if (hearingUpdateEvent.hearing() != null) {
            updateIndex(hearingUpdateEvent.hearing());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        final int bulkSize = 500;
        Queue<HearingId> hearingIdQueue =
                new ArrayDeque<>(hearingDataService.getHearingIds(SortOrder.DESC, LimitOffset.ALL));
        while(!hearingIdQueue.isEmpty()) {
            List<Hearing> hearings = new ArrayList<>(bulkSize);
            for (int i = 0; i < bulkSize && !hearingIdQueue.isEmpty(); i++) {
                HearingId hid = hearingIdQueue.remove();
                hearings.add(hearingDataService.getHearing(hid));
            }
            updateIndex(hearings);
        }
        logger.info("Finished reindexing hearings.");
    }
}
