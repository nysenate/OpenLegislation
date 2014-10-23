package gov.nysenate.openleg.service.bill.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.search.ElasticBillSearchDao;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchIndexFlushEvent;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillUpdateEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ElasticBillSearchService implements BillSearchService
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchService.class);

    private ConcurrentHashMap<BaseBillId, Bill> updateCache = new ConcurrentHashMap<>();

    @Value("${elastic.search.index.bill.buffersize:100}")
    private int maxCacheSize;

    @Autowired
    protected Environment env;

    @Autowired
    protected EventBus eventBus;

    @Autowired
    protected ElasticBillSearchDao billSearchDao;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff) throws SearchException {
        if (limOff == null) {
            limOff = LimitOffset.TEN;
        }
        try {
            return billSearchDao.searchBills(query, sort, limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }

    @Subscribe
    public void handleUpdate(BillUpdateEvent billUpdateEvent) {
        if (env.isElasticIndexing()) {
            Bill bill = billUpdateEvent.getBill();
            if (bill != null) {
                updateCache.put(bill.getBaseBillId(), bill);
            }
            checkIndexBuffer();
        }
    }

    @Scheduled(fixedDelay = 120000)
    public synchronized void checkIndexBuffer() {
        if (env.isElasticIndexing() && updateCache.size() >= maxCacheSize) {
            pushUpdatesToIndex();
        }
    }

    @Subscribe
    public void handleFlushEvent(SearchIndexFlushEvent flushEvent) {
        if (env.isElasticIndexing()) {
            pushUpdatesToIndex();
        }
    }

    public synchronized void pushUpdatesToIndex() {
      if (env.isElasticIndexing()) {
          if (!updateCache.isEmpty()) {
              try {
                  billSearchDao.updateBillIndices(updateCache.values());
              }
              catch (ElasticsearchException ex) {
                  logger.info("{}", ex);
                  System.exit(-1);
              }
              logger.debug("Updated bill search index with {} bills", updateCache.size());
              updateCache.clear();
          }
       }
    }
}
