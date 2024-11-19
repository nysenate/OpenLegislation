package gov.nysenate.openleg.api.admin;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.search.view.SearchIndexInfoView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.IndexedSearchService;
import gov.nysenate.openleg.search.SearchIndex;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/index")
public class SearchIndexCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(SearchIndexCtrl.class);
    public final ImmutableMap<SearchIndex, IndexedSearchService<?>> indexToSearchServiceMap;

    @Autowired
    public SearchIndexCtrl(List<IndexedSearchService<?>> searchServices) {
        var tempMap = searchServices.stream().collect(
                Collectors.toMap(IndexedSearchService::getIndex, Function.identity())
        );
        this.indexToSearchServiceMap = ImmutableMap.copyOf(tempMap);
    }

    @RequiresPermissions("admin:searchIndexEdit")
    @GetMapping(value = "")
    public ListViewResponse<SearchIndexInfoView> getIndices() {
        List<SearchIndexInfoView> names = Arrays.stream(SearchIndex.values())
                .map(SearchIndexInfoView::new).toList();
        return ListViewResponse.of(names, names.size(), LimitOffset.ALL);
    }

    /**
     * Search Index Rebuild API
     * Rebuild the specified search indices: (PUT) /api/3/admin/index/{indexType}
     * 'indexType' can be set to 'all' to reindex everything, or to one of the values in the
     * {@link SearchIndex} enumeration.
     * Re-indexing in this context means dropping all the existing data in an index and re-inserting
     * using data pulled from the backing store. Probably don't want to do this while a data processing
     * job is running.
     */
    @RequiresPermissions("admin:searchIndexEdit")
    @PutMapping(value = "/{indexType}")
    public BaseResponse rebuildIndex(@PathVariable String indexType) {
        try {
            for (SearchIndex index : getTargetIndices(indexType)) {
                var searchService = indexToSearchServiceMap.get(index);
                if (searchService == null) {
                    continue;
                }
                searchService.clearIndex();
                searchService.rebuildIndex();
                logger.info("Cleared {}{} index", "and rebuilt ", index.getName());
            }
            return new SimpleResponse(true, "Search index rebuild request completed", "index-clear");
        }
        catch (IllegalArgumentException ex) {
            var response = new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
            response.setMessage("Invalid search index: " + indexType);
            return response;
        }
    }

    private static Set<SearchIndex> getTargetIndices(String indexType) throws IllegalArgumentException {
        if (indexType.equalsIgnoreCase("all")) {
            // Exclude principal indices from "all" grouping.
            return SearchIndex.nonPrimaryIndices;
        }
        else {
            return Set.of(SearchIndex.valueOf(indexType.toUpperCase()));
        }
    }
}
