package gov.nysenate.openleg.service.hearing.data.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.hearing.search.ElasticHearingSearchDao;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.hearing.data.Event.BulkPublicHearingUpdateEvent;
import gov.nysenate.openleg.service.hearing.data.Event.PublicHearingUpdateEvent;
import gov.nysenate.openleg.service.hearing.data.PublicHearingDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticHearingSearchService implements HearingSearchService, IndexedSearchService<PublicHearing>
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticHearingSearchService.class);

    @Autowired protected Environment env;
    @Autowired protected EventBus eventBus;
    @Autowired protected ElasticHearingSearchDao publicHearingSearchDao;
    @Autowired protected PublicHearingDataService publicHearingDataService;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(String query, String sort, LimitOffset limOff) throws SearchException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(String query, int year, String sort, LimitOffset limOff) throws SearchException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handlePublicHearingUpdate(PublicHearingUpdateEvent publicHearingUpdateEvent) {
        if (publicHearingUpdateEvent.getPublicHearing() != null) {
            updateIndex(publicHearingUpdateEvent.getPublicHearing());
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleBulkPublicHearingUpdate(BulkPublicHearingUpdateEvent bulkPublicHearingUpdateEvent) {
        if (bulkPublicHearingUpdateEvent.getPublicHearings() != null) {
            updateIndex(bulkPublicHearingUpdateEvent.getPublicHearings());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(PublicHearing publicHearing) {
        if (env.isElasticIndexing() && publicHearing != null) {
            logger.info("Indexing public hearing {} into elastic search.", publicHearing.getTitle());
            publicHearingSearchDao.updatePublicHearingIndex(publicHearing);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<PublicHearing> publicHearings) {
        if (env.isElasticIndexing() && !publicHearings.isEmpty()) {
            List<PublicHearing> indexablePublicHearings = publicHearings.stream().filter(ph -> ph != null).collect(Collectors.toList());
            logger.info("Indexing {} public hearings into elastic search.", indexablePublicHearings.size());
            publicHearingSearchDao.updatePublicHearingIndex(indexablePublicHearings);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        publicHearingSearchDao.purgeIndices();
        publicHearingSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        for (int year = 2011; year <= LocalDate.now().getYear(); year++) {
            LimitOffset limitOffset = LimitOffset.TWENTY_FIVE;
            List<PublicHearingId> publicHearingIds = publicHearingDataService.getPublicHearingIds(year, SortOrder.DESC, limitOffset);
            while (!publicHearingIds.isEmpty()) {
                logger.info("Indexing {} public hearings starting from {}.", publicHearingIds.size(), year);
                List<PublicHearing> publicHearings = publicHearingIds.stream().map(publicHearingDataService::getPublicHearing).collect(Collectors.toList());
                limitOffset = limitOffset.next();
                publicHearingIds = publicHearingDataService.getPublicHearingIds(year, SortOrder.DESC, limitOffset);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.HEARING)) {
            logger.info("Handling public hearing re-index event.");
            try {
                rebuildIndex();
            } catch (Exception ex) {
                logger.error("Unexpected exception during handling of public hearing index rebuild event.", ex);
            }
        }
    }
}
