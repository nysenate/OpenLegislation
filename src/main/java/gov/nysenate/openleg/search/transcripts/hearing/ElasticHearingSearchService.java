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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticHearingSearchService extends IndexedSearchService<Hearing> implements HearingSearchService {
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
        return hearingSearchDao.searchForIds(queryStr, sort, limOff, getYearRangeQuery("date", year));
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
        updateIndex(
                hearingDataService.getHearingIds(SortOrder.DESC, LimitOffset.ALL).stream()
                        .map(hearingDataService::getHearing).toList()
        );
    }
}
