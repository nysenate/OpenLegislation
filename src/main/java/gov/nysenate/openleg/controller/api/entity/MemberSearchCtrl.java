package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.SessionMember;
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
    @Autowired private MemberService memberData;
    @Autowired private MemberSearchService memberSearch;

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
    public BaseResponse globalSearch(@RequestParam(required = true) String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<SessionMember> results = memberSearch.searchMembers(term, sort, limOff);
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
    @RequestMapping(value = "/{sessionYear}/search")
    public BaseResponse globalSearch(@PathVariable int sessionYear,
                                     @RequestParam(required = true) String term,
                                     @RequestParam(defaultValue = "") String sort,
                                     @RequestParam(defaultValue = "false") boolean full,
                                     WebRequest webRequest) throws SearchException {
        LimitOffset limOff = getLimitOffset(webRequest, 50);
        SearchResults<SessionMember> results = memberSearch.searchMembers(term, SessionYear.of(sessionYear), sort, limOff);
        return getSearchResponse(results, full, limOff);
    }

    private BaseResponse getSearchResponse(SearchResults<SessionMember> results, boolean full, LimitOffset limOff) throws SearchException {
        List<ViewObject> viewtypes = new ArrayList<>();
        for (SearchResult<SessionMember> result : results.getResults()) {
            SessionMember member;
            try {
                member = memberData.getMemberById(result.getResult().getMemberId(), result.getResult().getSessionYear());
            } catch (MemberNotFoundEx ex) {
                throw new SearchException("No Member found.", ex);
            }
            viewtypes.add((full) ? new MemberView(member) : new SimpleMemberView(member));
        }
        return ListViewResponse.of(viewtypes, results.getTotalResults(), limOff);
    }
}
