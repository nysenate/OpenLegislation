package gov.nysenate.openleg.api.logs;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.logs.ApiLogSearchService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/apiLogs")
public class ApiLogCtrl extends BaseCtrl {
    private final ApiLogSearchService logSearchService;

    @Autowired
    public ApiLogCtrl(ApiLogSearchService logSearchService) {
        this.logSearchService = logSearchService;
    }

    @RequiresPermissions("admin:apilog:view")
    @RequestMapping("")
    public BaseResponse searchLogs(@RequestParam(defaultValue = "*") String term,
                                   @RequestParam(defaultValue = "requestTime:DESC") String sort,
                                    WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<ApiLogItemView> results = logSearchService.searchApiLogs(term, sort, limOff);
        return ListViewResponse.of(
            results.resultList().stream()
                .map(r -> new SearchResultView(r.result(), r.rank(), r.highlights()))
                .toList(), results.totalResults(), limOff);
    }
}
