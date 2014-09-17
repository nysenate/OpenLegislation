package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchResult;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.search.BillSearchField;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/bills/search", method = RequestMethod.GET)
public class BillSearchCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillSearchCtrl.class);

    @Autowired
    private BillSearchService billSearch;

    /** --- Request Handlers --- */

    @RequestMapping(value = "")
    public SearchResults<BillId> advancedSearch(WebRequest webRequest,
                                                @RequestParam(defaultValue = "25") int limit,
                                                @RequestParam(defaultValue = "1") int offset) {
        LimitOffset limOff = new LimitOffset(limit, offset);
        Map<BillSearchField, String> queryMap = getAdvancedSearchQueryMap(webRequest);
        logger.info("{}", queryMap);
        SearchResults<BillId> results = billSearch.searchAdvanced(queryMap, limOff);
        logger.info("{}", results);
        return results;
    }

    @RequestMapping(value = "/{term}")
    public SearchResults<BillId> globalSearch(@PathVariable String term,
                                             @RequestParam(defaultValue = "25") int limit,
                                             @RequestParam(defaultValue = "1") int offset) {
        logger.debug("Bill Search Request: {}", term);
        LimitOffset limOff = new LimitOffset(limit, offset);
        return billSearch.searchAll(term, limOff);
    }

    /** --- Internal Methods --- */

    /**
     * From the collection of query parameters derive a query map which associates a valid
     * BillSearchField to a search term.
     *
     * @param webRequest
     * @return Map<BillSearchField, String>
     */
    private Map<BillSearchField, String> getAdvancedSearchQueryMap(WebRequest webRequest) {
        return webRequest.getParameterMap().entrySet().stream()
            // Look for request parameters that have a valid bill search field mapping
            .filter(e -> BillSearchField.isValidParam(e.getKey()))
            // Flatten the entries into a map, with the request param value arrays converted into strings
            .collect(Collectors.toMap(p -> BillSearchField.valueOfParam(p.getKey()),
                    p -> StringUtils.join(Arrays.asList(p.getValue()), " ")));
    }
}