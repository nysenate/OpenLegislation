package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.log.ApiLogItemView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.log.search.ApiLogStompService;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResult;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.auth.ApiUserService;
import gov.nysenate.openleg.service.log.search.ApiLogSearchService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/apiLogs")
public class ApiLogCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(ApiLogCtrl.class);

    @Autowired private ApiLogSearchService logSearchService;
    @Autowired private ApiUserService apiUserService;

    @RequiresPermissions("admin:apilog:view")
    @RequestMapping("")
    public BaseResponse searchLogs(@RequestParam(defaultValue = "*") String term,
                                   @RequestParam(defaultValue = "requestTime:DESC") String sort,
                                    WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<ApiLogItemView> results = logSearchService.searchApiLogs(term, sort, limOff);
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r -> new SearchResultView(r.getResult(), r.getRank(), r.getHighlights()))
                .collect(toList()), results.getTotalResults(), limOff);
    }
}
