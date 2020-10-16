package gov.nysenate.openleg.controller.api.law;

import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.law.LawDocInfoView;
import gov.nysenate.openleg.client.view.search.SearchResultView;
import gov.nysenate.openleg.controller.api.LawCtrlTest;
import gov.nysenate.openleg.model.law.LawChapterCode;
import gov.nysenate.openleg.model.law.LawType;
import gov.nysenate.openleg.model.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.law.LawChapterCode.EHC;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class LawSearchCtrlTest extends LawCtrlTest {

    @Autowired
    private LawSearchCtrl testCtrl;

    @Test
    public void testAllSearchLaws() {
        for (String fileId : TEST_LAW_IDS)
            loadTestData(fileId, true);
        try {
            List<String> lawIds = convertResponse(testCtrl.searchLaws("locationId:1", testRequest));
            List<String> ruleLawIds = convertResponse(testCtrl.searchLaws("locationId:R1", testRequest));
            for (String lawId : TEST_LAW_IDS) {
                if (LawChapterCode.valueOf(lawId).getType() == LawType.RULES)
                    assertTrue(ruleLawIds.contains(lawId));
                else
                    assertTrue(lawIds.contains(lawId));
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
        List<SearchResultView> asSrv = asListView.getItems().stream().map(item -> ((SearchResultView) item)).collect(Collectors.toList());
        List<LawDocInfoView> asLdiv = asSrv.stream().map(srv -> ((LawDocInfoView) srv.getResult())).collect(Collectors.toList());
        return asLdiv.stream().map(LawDocInfoView::getLawId).collect(Collectors.toList());
    }
}
