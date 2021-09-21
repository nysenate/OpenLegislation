package gov.nysenate.openleg.api.legislation.law;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.legislation.law.view.LawDocInfoView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.search.view.SearchResultView;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawType;
import gov.nysenate.openleg.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.legislation.law.LawChapterCode.EHC;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class LawSearchCtrlIT extends LawCtrlBaseIT {

    @Autowired
    private LawSearchCtrl testCtrl;

    @Test
    public void testAllSearchLaws() {
        addParam("limit", "all");
        for (String fileId : TEST_LAW_IDS)
            loadTestData(fileId, true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        try {
            List<String> lawIds = convertResponse(testCtrl.searchLaws("locationId:1", testRequest));
            List<String> ruleLawIds = convertResponse(testCtrl.searchLaws("locationId:R1", testRequest));
            for (String lawId : TEST_LAW_IDS) {
                String message = "Search responses do not contain law chapter " + lawId;
                if (LawChapterCode.valueOf(lawId).getType() == LawType.RULES)
                    assertTrue(message, ruleLawIds.contains(lawId));
                else {
                    assertTrue(message, lawIds.contains(lawId));
                }
            }
        } catch (SearchException e) {
            fail();
        }
    }

    @Test
    public void testSearchLaws() {
        loadTestData(EHC.name(), true);
        try {
            ListViewResponse<?> response = (ListViewResponse<?>) testCtrl.searchLaws(EHC.name(), "title:Short", testRequest);
            assertEquals(1, response.getTotal());
        } catch (SearchException e) {
            fail();
        }
    }

    private static List<String> convertResponse(BaseResponse response) {
        ListView<?> asListView = ((ListViewResponse<?>) response).getResult();
        return asListView.getItems().stream().map(item -> ((SearchResultView) item)).
                map(srv -> ((LawDocInfoView) srv.getResult())).map(LawDocInfoView::getLawId).collect(Collectors.toList());
    }
}
