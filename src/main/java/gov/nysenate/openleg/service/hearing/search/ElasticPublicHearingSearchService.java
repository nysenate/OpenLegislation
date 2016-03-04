package gov.nysenate.openleg.service.hearing.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.hearing.search.ElasticPublicHearingSearchDao;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.search.*;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.hearing.event.BulkPublicHearingUpdateEvent;
import gov.nysenate.openleg.service.hearing.event.PublicHearingUpdateEvent;
import gov.nysenate.openleg.service.hearing.data.PublicHearingDataService;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchParseException;
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
public class ElasticPublicHearingSearchService implements PublicHearingSearchService, IndexedSearchService<PublicHearing>
{
    private static final Logger logger = LoggerFactory.getLogger(PublicHearingSearchService.class);

    @Autowired protected Environment env;
    @Autowired protected EventBus eventBus;
    @Autowired protected ElasticPublicHearingSearchDao publicHearingSearchDao;
    @Autowired protected PublicHearingDataService publicHearingDataService;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.matchAllQuery(), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(int year, String sort, LimitOffset limOff) throws SearchException {
        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter("date")
                .from(LocalDate.of(year, 1, 1))
                .to(LocalDate.of(year, 12, 31))
                .cache(false);
        return search(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), rangeFilter), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(String query, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.queryString(query), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<PublicHearingId> searchPublicHearings(String query, int year, String sort, LimitOffset limOff) throws SearchException {
        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter("date")
                .from(LocalDate.of(year, 1, 1))
                .to(LocalDate.of(year, 12, 31))
                .cache(false);
        return search(QueryBuilders.filteredQuery(QueryBuilders.queryString(query), rangeFilter), null, sort, limOff);
    }

    private SearchResults<PublicHearingId> search(QueryBuilder query, FilterBuilder postFilter, String sort, LimitOffset limOff)
            throws SearchException {
        if (limOff == null) limOff = LimitOffset.TEN;
        try {
            return publicHearingSearchDao.searchPublicHearings(query, postFilter,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex);
        }
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
            List<PublicHearingId> publicHearingIds = publicHearingDataService.getPublicHearingIds(SortOrder.DESC, limitOffset);
            while (!publicHearingIds.isEmpty()) {
                logger.info("Indexing {} public hearings starting from {}.", publicHearingIds.size(), year);
                List<PublicHearing> publicHearings = publicHearingIds.stream().map(publicHearingDataService::getPublicHearing).collect(Collectors.toList());
                updateIndex(publicHearings);
                limitOffset = limitOffset.next();
                publicHearingIds = publicHearingDataService.getPublicHearingIds(SortOrder.DESC, limitOffset);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
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

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.HEARING)) {
            clearIndex();
        }
    }
}
