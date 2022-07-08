package gov.nysenate.openleg.api.admin;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.search.view.SearchIndexInfoView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.ClearIndexEvent;
import gov.nysenate.openleg.search.RebuildIndexEvent;
import gov.nysenate.openleg.search.SearchIndex;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/index")
public class SearchIndexCtrl extends BaseCtrl {
    private final EventBus eventBus;

    @Autowired
    public SearchIndexCtrl(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /**
     * Search Index Rebuild API
     *
     * Rebuild the specified search indices: (PUT) /api/3/admin/index/{indexType}
     * 'indexType' can be set to 'all' to reindex everything, or to one of the values in the
     * {@link SearchIndex} enumeration.
     *
     * Re-indexing in this context means dropping all the existing data in an index and re-inserting
     * using data pulled from the backing store. Probably don't want to do this while a data processing
     * job is running.
     */
    @RequiresPermissions("admin:searchIndexEdit")
    @PutMapping(value = "/{indexType}")
    public BaseResponse rebuildIndex(@PathVariable String indexType) {
        return clearIndex(indexType, true);
    }

    @RequiresPermissions("admin:searchIndexEdit")
    @DeleteMapping(value = "/{indexType}")
    public BaseResponse clearIndex(@PathVariable String indexType) {
        return clearIndex(indexType, false);
    }

    private BaseResponse clearIndex(String indexType, boolean rebuild) {
        try {
            Set<SearchIndex> targetIndices = getTargetIndices(indexType);
            eventBus.post(rebuild ? new RebuildIndexEvent(targetIndices) :
                    new ClearIndexEvent(targetIndices));
            return new SimpleResponse(true, "Search index " + (rebuild ? "rebuild" : "clear")
                    + " request completed", "index-clear");
        }
        catch (IllegalArgumentException ex) {
            var response = new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
            response.setMessage("Invalid search index: " + indexType);
            return response;
        }
    }

    @RequiresPermissions("admin:searchIndexEdit")
    @GetMapping(value = "")
    public ListViewResponse<SearchIndexInfoView> getIndices() {
        List<SearchIndexInfoView> names = Arrays.stream(SearchIndex.values())
                .map(SearchIndexInfoView::new).toList();
        return ListViewResponse.of(names, names.size(), LimitOffset.ALL);
    }

    /** --- Internal --- */

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
