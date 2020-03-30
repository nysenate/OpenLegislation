package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.client.view.entity.SessionMemberView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResult;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.service.entity.member.search.MemberSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/members", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class MemberSearchCtrl extends BaseCtrl
{
    @Autowired protected MemberService memberData;
    @Autowired protected MemberSearchService memberSearch;

    /**
     * Member Search API
     * ---------------------
     *
     * Search all members:  (GET) /api/3/members/search
     * Request Parameters:  term - The lucene query string.
     *                      sort - The lucene sort string (blank by default)
     *                      full - If true, returns the full member view
     *                      limit - Limit the number of results (default 50)
     *                      offset - Start results from offset
     */
    @RequestMapping(value = "/search")
    public BaseResponse globalSearch(@RequestParam String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<Integer> results = memberSearch.searchMembers(term, sort, limOff);
        return getSearchResponse(results, full, limOff);
    }

    /**
     * Member Search API
     * ---------------------
     *
     * Search all members by year:  (GET) /api/3/members/{sessionYear}/search
     * Request Parameters:  term - The lucene query string.
     *                      sort - The lucene sort string (blank by default)
     *                      full - If true, returns the full member view
     *                      limit - Limit the number of results (default 50)
     *                      offset - Start results from offset
     */
    @RequestMapping(value = "/{sessionYear:\\d{4}}/search")
    public BaseResponse globalSearch(@PathVariable int sessionYear,
                                     @RequestParam String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<Integer> results = memberSearch.searchMembers(term, SessionYear.of(sessionYear), sort, limOff);
        return getSearchResponse(results, full, limOff);
    }

    private BaseResponse getSearchResponse(SearchResults<Integer> results, boolean full, LimitOffset limOff) throws SearchException {
        List<ViewObject> viewTypes = new ArrayList<>();
        for (SearchResult<Integer> result : results.getResults()) {
            FullMember member;
            try {
                member = memberData.getFullMemberById(result.getResult());
            } catch (MemberNotFoundEx ex) {
                throw new SearchException("No Member found.", ex);
            }
            viewTypes.add((full) ? new FullMemberView(member) : new SessionMemberView(member.getLatestSessionMember().get()));
        }
        return ListViewResponse.of(viewTypes, results.getTotalResults(), limOff);
    }
}
