package gov.nysenate.openleg.controller.api.law;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.SearchResultView;
import gov.nysenate.openleg.client.view.law.LawDocIdView;
import gov.nysenate.openleg.client.view.law.LawDocInfoView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.search.LawSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BaseCtrl.BASE_API_PATH + "/laws", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class LawSearchCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(LawSearchCtrl.class);

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
    public BaseResponse searchLaws(@RequestParam(required = true) String term, WebRequest request)
                                   throws SearchException {
        return searchLaws(null, term, request);
    }

    @RequestMapping("/{lawId}/search")
    public BaseResponse searchLaws(@PathVariable String lawId, @RequestParam(required = true) String term,
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
                .collect(toList()), results.getTotalResults(), limOff);
    }
}
