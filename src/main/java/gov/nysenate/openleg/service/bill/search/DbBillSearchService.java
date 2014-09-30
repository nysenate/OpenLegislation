package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.search.BillSearchDao;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Performs bill search functions using an in-database approach where the search data is
 * automatically synchronized by the database whenever source data is modified.
 *
 * This implementation favors consistency and ease of integration over performance. Alternative
 * search implementations designed for performance can be designed using an external search provider.
 */
@Service
public class DbBillSearchService implements BillSearchService
{
    private static final Logger logger = LoggerFactory.getLogger(DbBillSearchService.class);

    @Resource(name = "dbBillSearch")
    private BillSearchDao billSearchDao;

    /** --- Implemented Methods --- */

    @Override
    public SearchResults<BillId> searchAll(String query, LimitOffset limOff) {
        query = sanitizeQuery(query);
        return billSearchDao.searchAll(query, limOff);
    }

    @Override
    public SearchResults<BillId> searchAdvanced(Map<BillSearchField, String> criteria, LimitOffset limOff) {
        if (criteria == null) {
            throw new IllegalArgumentException("Supplied criteria for advanced search cannot be null");
        }
        Map<BillSearchField, String> cleanCriteria = new HashMap<>();
        criteria.forEach((k, v) -> {
            cleanCriteria.put(k, sanitizeQuery(v));
        });
        return billSearchDao.searchAdvanced(cleanCriteria, limOff);
    }

    /** --- Internal Methods --- */

    /**
     * Performs basic replacements on the string to improve search results.
     * @param query String
     * @return String
     */
    protected String sanitizeQuery(String query) {
        if (query == null) {
            query = "";
        }
        return query.replaceAll("[-/\\(\\)~]", " ").trim();
    }
}
