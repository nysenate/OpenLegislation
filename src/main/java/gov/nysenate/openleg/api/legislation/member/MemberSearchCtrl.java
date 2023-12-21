package gov.nysenate.openleg.api.legislation.member;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.member.view.FullMemberView;
import gov.nysenate.openleg.api.legislation.member.view.SessionMemberView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.member.MemberSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/members", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class MemberSearchCtrl extends BaseCtrl {
    private final MemberService memberData;
    private final MemberSearchService memberSearch;

    @Autowired
    public MemberSearchCtrl(MemberService memberData, MemberSearchService memberSearch) {
        this.memberData = memberData;
        this.memberSearch = memberSearch;
    }

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
        for (SearchResult<Integer> result : results.resultList()) {
            FullMember member;
            try {
                member = memberData.getFullMemberById(result.result());
            } catch (MemberNotFoundEx ex) {
                throw new SearchException("No Member found.", ex);
            }
            viewTypes.add((full) ? new FullMemberView(member) : new SessionMemberView(member.getLatestSessionMember().get()));
        }
        return ListViewResponse.of(viewTypes, results.totalResults(), limOff);
    }
}
