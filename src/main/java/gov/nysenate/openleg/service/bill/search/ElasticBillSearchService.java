package gov.nysenate.openleg.service.bill.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.search.ElasticBillSearchDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.service.base.SearchException;
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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class ElasticBillSearchService implements BillSearchService
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchService.class);

    @Autowired
    protected EventBus eventBus;

    @Autowired
    protected ElasticBillSearchDao billSearchDao;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    @Value("${my.awesome.string}")
    protected String myValue;

    @Override
    public SearchResults<BaseBillId> searchBills(String query, LimitOffset limOff) throws SearchException {
        if (limOff == null) {
            limOff = LimitOffset.TEN;
        }
        try {
            return billSearchDao.searchBills(query, limOff);
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
        logger.info("Got here!");
    }
}
