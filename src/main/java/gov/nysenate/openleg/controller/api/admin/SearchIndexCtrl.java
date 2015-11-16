package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.search.ClearIndexEvent;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Set;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/index")
public class SearchIndexCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SearchIndexCtrl.class);

    @Autowired private EventBus eventBus;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /**
     * Search Index Rebuild API
     *
     * Rebuild the specified search indices: (PUT) /api/3/admin/index/{indexType}
     * 'indexType' can be set to 'all' to reindex everything, or to one of the values in the
     * {@link gov.nysenate.openleg.dao.base.SearchIndex} enumeration.
     *
     * Re-indexing in this context means dropping all the existing data in an index and re-inserting
     * using data pulled from the backing store. Probably don't want to do this while a data processing
     * job is running.
     */
    @RequiresPermissions("admin:searchIndexEdit")
    @RequestMapping(value = "/{indexType}", method = RequestMethod.PUT)
    public BaseResponse rebuildIndex(@PathVariable String indexType) {
        BaseResponse response;
        try {
            Set<SearchIndex> targetIndices = getTargetIndices(indexType);
            eventBus.post(new RebuildIndexEvent(targetIndices));
            response = new SimpleResponse(true, "Search index rebuild request completed", "index-rebuild");
        }
        catch (IllegalArgumentException ex) {
            response = new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
            response.setMessage("Invalid search index: " + indexType);
        }
        return response;
    }

    @RequiresPermissions("admin:searchIndexEdit")
    @RequestMapping(value = "/{indexType}", method = RequestMethod.DELETE)
    public BaseResponse clearIndex(@PathVariable String indexType) {
        BaseResponse response;
        try {
            Set<SearchIndex> targetIndices = getTargetIndices(indexType);
            eventBus.post(new ClearIndexEvent(targetIndices));
            response = new SimpleResponse(true, "Search index clear request completed", "index-clear");
        }
        catch (IllegalArgumentException ex) {
            response = new ErrorResponse(ErrorCode.INVALID_ARGUMENTS);
            response.setMessage("Invalid search index: " + indexType);
        }
        return response;
    }

    /** --- Internal --- */

    private Set<SearchIndex> getTargetIndices(String indexType) throws IllegalArgumentException {
        Set<SearchIndex> targetIndices;
        if (indexType.equalsIgnoreCase("all")) {
            targetIndices = Sets.newHashSet(SearchIndex.values());
        }
        else {
            targetIndices = Sets.newHashSet(SearchIndex.valueOf(indexType.toUpperCase()));
        }
        return targetIndices;
    }
}
