package gov.nysenate.openleg.api.legislation.law;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.law.view.LawDocInfoView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.law.LawSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BaseCtrl.BASE_API_PATH + "/laws", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class LawSearchCtrl extends BaseCtrl
{
    @Autowired private LawDataService lawData;
    @Autowired private LawSearchService lawSearch;

    /**
     * Law Search API
     * --------------
     *
     * Search across law documents.
     *
     * Usage:
     * /api/3/laws/search           (all docs)
     * /api/3/laws/{lawId}/search   (docs within lawId)
     *
     * Request Params; term - Lucene query string
     *                 sort - Lucene sort string
     *                 limit, offset - Pagination
     *
     * Expected Output: List of SearchResultView<LawDocInfoView>>
     */

    @RequestMapping("/search")
    public BaseResponse searchLaws(@RequestParam String term, WebRequest request)
                                   throws SearchException {
        return searchLaws(null, term, request);
    }

    @RequestMapping("/{lawId}/search")
    public BaseResponse searchLaws(@PathVariable String lawId, @RequestParam String term,
                                   WebRequest request) throws SearchException {
        LimitOffset limOff = getLimitOffset(request, 25);
        String sort = request.getParameter("sort");
        return getLawDocSearchResults(limOff, lawSearch.searchLawDocs(term, lawId, sort, limOff));
    }

    /** --- Internal --- */

    private BaseResponse getLawDocSearchResults(LimitOffset limOff, SearchResults<LawDocId> results) {
        return ListViewResponse.of(
            results.getResults().stream()
                .map(r -> new SearchResultView(
                    new LawDocInfoView(lawData.getLawDocInfo(r.getResult().getDocumentId(), r.getResult().getPublishedDate())),
                        r.getRank(), r.getHighlights()))
                .toList(), results.getTotalResults(), limOff);
    }
}
