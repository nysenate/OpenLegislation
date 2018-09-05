package gov.nysenate.openleg.dao.bill.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ElasticBillSearchDao extends ElasticBaseDao implements BillSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchDao.class);

    private static final int billMaxResultWindow = 500000;

    protected static final String billIndexName = SearchIndex.BILL.getIndexName();

    protected static final List<HighlightBuilder.Field> highlightedFields =
        Arrays.asList(new HighlightBuilder.Field("basePrintNo").numOfFragments(0),
                      new HighlightBuilder.Field("printNo").numOfFragments(0),
                      new HighlightBuilder.Field("title").numOfFragments(0));

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(QueryBuilder query, QueryBuilder postFilter, RescorerBuilder rescorer,
                                                 List<SortBuilder> sort, LimitOffset limOff) {
        return search(billIndexName, query, postFilter,
                highlightedFields, rescorer, sort, limOff,
                false, this::getBaseBillIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Bill bill) {
            updateBillIndex(Collections.singletonList(bill));
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Collection<Bill> bills) {
        BulkRequest bulkRequest = new BulkRequest();
        bills.stream()
                .map(BillView::new)
                .map(bv -> getJsonIndexRequest(billIndexName, toElasticId(bv.toBaseBillId()), bv))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBillFromIndex(BaseBillId baseBillId) {
        if (baseBillId != null) {
            deleteEntry(billIndexName, toElasticId(baseBillId));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(billIndexName);
    }

    /**
     * Increase max result window for bills in order to perform paginated queries on lots of bills.
     *
     * @see gov.nysenate.openleg.controller.api.bill.BillGetCtrl#getBills(int, String, boolean, boolean, WebRequest)
     *
     * @return int
     */
    @Override
    protected int getMaxResultWindow() {
        return billMaxResultWindow;
    }

    protected BaseBillId getBaseBillIdFromHit(SearchHit hit) {

        String[] IDparts = hit.getId().split("-");

        return new BaseBillId(IDparts[1], Integer.parseInt(IDparts[0]));
    }

    private String toElasticId(BaseBillId baseBillId) {
        return baseBillId.getSession() + "-" +
                baseBillId.getBasePrintNo();
    }
}