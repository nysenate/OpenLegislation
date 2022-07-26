package gov.nysenate.openleg.api.legislation.committee;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeVersionIdView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.CommitteeNotFoundEx;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.committee.CommitteeSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/committees", method = RequestMethod.GET)
public class CommitteeSearchCtrl extends BaseCtrl {
    private final CommitteeSearchService committeeSearchService;
    private final CommitteeDataService committeeDataService;

    @Autowired
    public CommitteeSearchCtrl(CommitteeSearchService committeeSearchService, CommitteeDataService committeeDataService) {
        this.committeeSearchService = committeeSearchService;
        this.committeeDataService = committeeDataService;
    }

    /** --- Request Handlers --- */

    /**
     * Committee Search API
     *
     * Search all committees:   (GET) /api/3/committees/search
     * Request Parameters:      term - The lucene query string
     *                          sort - The lucene sort string (blank by default)
     *                          latest - Set to true to filter out all committee versions that are not the latest
     *                              version for a session (true by default)
     *                          full - Set to true to retrieve full committee responses (false by default)
     *                          limit - Limit the number of results (default 25)
     *                          offset - Start results from offset (default 1)
     */
    @RequestMapping(value = "/search")
    public BaseResponse searchAllCommittees(@RequestParam String term,
                                            @RequestParam(defaultValue = "") String sort,
                                            @RequestParam(defaultValue = "true") boolean current,
                                            @RequestParam(defaultValue = "false") boolean full,
                                            WebRequest webRequest) throws SearchException, CommitteeNotFoundEx {
        LimitOffset limitOffset = getLimitOffset(webRequest, 50);
        SearchResults<CommitteeVersionId> searchResults = current
                ? committeeSearchService.searchAllCurrentCommittees(term, sort, limitOffset)
                : committeeSearchService.searchAllCommittees(term, sort, limitOffset);
        return getCommitteeSearchResponse(searchResults, full);
    }

    /**
     * Committee Search by Session API
     *
     * Search all committees in a given session year: (GET) /api/3/committees/{year}/search
     * @see #searchAllCommittees for request params
     */
    @RequestMapping(value = "/{year:\\d{4}}/search")
    public BaseResponse searchCommitteesForSession(@PathVariable int year,
                                                   @RequestParam String term,
                                                   @RequestParam(defaultValue = "") String sort,
                                                   @RequestParam(defaultValue = "true") boolean current,
                                                   @RequestParam(defaultValue = "false") boolean full,
                                                   WebRequest webRequest) throws SearchException, CommitteeNotFoundEx {
        SessionYear sessionYear = SessionYear.of(year);
        LimitOffset limitOffset = getLimitOffset(webRequest, 50);
        SearchResults<CommitteeVersionId> searchResults = current
                ? committeeSearchService.searchCurrentCommitteesForSession(sessionYear, term, sort, limitOffset)
                : committeeSearchService.searchCommitteesForSession(sessionYear, term, sort, limitOffset);
        return getCommitteeSearchResponse(searchResults, full);
    }

    /**
     * --- Internal Methods ---
     */

    protected BaseResponse getCommitteeSearchResponse(SearchResults<CommitteeVersionId> results, boolean full)
            throws CommitteeNotFoundEx {
        return ListViewResponse.of(
                results.resultList().stream()
                        .map(result -> new SearchResultView(full
                                ? new CommitteeView(committeeDataService.getCommittee(result.result()))
                                : new CommitteeVersionIdView(result.result()),
                                result.rank())).toList(),
                results.totalResults(), results.limitOffset()
        );
    }
}
