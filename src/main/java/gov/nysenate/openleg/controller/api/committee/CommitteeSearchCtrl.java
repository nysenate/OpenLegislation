package gov.nysenate.openleg.controller.api.committee;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.committee.CommitteeVersionIdView;
import gov.nysenate.openleg.client.view.committee.CommitteeView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.CommitteeNotFoundEx;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
import gov.nysenate.openleg.service.entity.committee.search.CommitteeSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(value = BASE_API_PATH + "/committees", method = RequestMethod.GET)
public class CommitteeSearchCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(CommitteeSearchCtrl.class);

    @Autowired
    CommitteeSearchService committeeSearchService;
    @Autowired
    CommitteeDataService committeeDataService;

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
    public BaseResponse searchAllCommittees(@RequestParam(required = true) String term,
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
                                                   @RequestParam(required = true) String term,
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
                results.getResults().stream()
                        .map(result -> new SearchResultView(full
                                ? new CommitteeView(committeeDataService.getCommittee(result.getResult()))
                                : new CommitteeVersionIdView(result.getResult()),
                                result.getRank()))
                        .collect(Collectors.toList()),
                results.getTotalResults(), results.getLimitOffset()
        );
    }
}
