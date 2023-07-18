package gov.nysenate.openleg.api.legislation.law;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.legislation.law.view.LawDocInfoView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.legislation.law.LawChapterCode.EHC;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class LawSearchCtrlIT extends LawCtrlBaseIT {

    @Autowired
    private LawSearchCtrl testCtrl;

    @Test
    public void allLawsSearch() {
        loadTestData(true, TEST_LAW_IDS);
        try {
            var lawIds = searchLaws("locationId:1", "locationId:R1");
            for (String lawId : TEST_LAW_IDS)
                assertTrue("Search responses do not contain law chapter " + lawId, lawIds.contains(lawId));
        } catch (SearchException e) {
            fail();
        }
    }

    @Test
    public void lawIdSearch() {
        loadTestData(true, EHC.name());
        try {
            ListViewResponse<?> response = (ListViewResponse<?>) testCtrl.searchLaws(EHC.name(), "title:Short", testRequest);
            assertEquals(1, response.getTotal());
        } catch (SearchException e) {
            fail();
        }
    }

    private List<String> searchLaws(String... terms) throws SearchException {
        var fullList = new ArrayList<String>();
        for (var term : terms)
            fullList.addAll(convertResponse(testCtrl.searchLaws(term, testRequest)));
        return fullList;
    }

    private static List<String> convertResponse(BaseResponse response) {
        ListView<?> asListView = ((ListViewResponse<?>) response).getResult();
        return asListView.getItems().stream().map(item -> ((SearchResultView) item)).
                map(srv -> ((LawDocInfoView) srv.getResult())).map(LawDocInfoView::getLawId).collect(Collectors.toList());
    }
}
