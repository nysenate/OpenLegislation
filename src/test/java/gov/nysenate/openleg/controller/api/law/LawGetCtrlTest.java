package gov.nysenate.openleg.controller.api.law;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.law.*;
import gov.nysenate.openleg.controller.api.LawCtrlTest;
import gov.nysenate.openleg.model.law.LawDocumentType;
import gov.nysenate.openleg.service.law.data.LawDocumentNotFoundEx;
import gov.nysenate.openleg.service.law.data.LawTreeNotFoundEx;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.openleg.model.law.LawChapterCode.*;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class LawGetCtrlTest extends LawCtrlTest {

    @Autowired
    private LawGetCtrl testCtrl;

    @Test
    public void getAllLawsTest() {
        for (String fileId : TEST_LAW_IDS)
            loadTestData(fileId, true);
        ImmutableList<?> genericAllLaws = ((ListViewResponse<?>)testCtrl.getLaws(testRequest)).getResult().getItems();
        assertTrue(TEST_LAW_IDS.size() <= genericAllLaws.size());
        List<String> lawIds = new ArrayList<>();
        for (Object o : genericAllLaws)
            lawIds.add(((LawInfoView)o).getLawId());
        for (String lawId : TEST_LAW_IDS)
            assertTrue(lawIds.contains(lawId));
    }

    @Test
    public void getLawTreeTest() {
        loadTestData(EHC.name(), true);
        loadTestData(CMA.name(), true);
        loadTestData(CMS.name(), true);
        ViewObjectResponse<?> genericTree = (ViewObjectResponse<?>)testCtrl.getLawTree(EHC.name(), null, null, null, false);
        LawTreeView tree = ((LawTreeView) genericTree.getResult());
        ImmutableList<LawNodeView> nodes = tree.getDocuments().getDocuments().getItems();
        for (int i = 1; i <= 4; i++) {
            assertEquals(Integer.toString(i), nodes.get(i-1).getFromSection());
            assertEquals(Integer.toString(i), nodes.get(i-1).getToSection());;
        }

        genericTree = (ViewObjectResponse<?>)testCtrl.getLawTree(CMA.name(), null, "R1", null, true);
        tree = ((LawTreeView) genericTree.getResult());
        nodes = tree.getDocuments().getDocuments().getItems();
        for (LawNodeView node : nodes)
            assertFalse(node.getLocationId().matches("(J|R2).*"));
        assertTrue(tree.getDocuments().getText().matches("RULE I\\\\n.*SPEAKER\\\\n.*"));

        genericTree = (ViewObjectResponse<?>)testCtrl.getLawTree(CMS.name(), null, null, 1, true);
        tree = ((LawTreeView) genericTree.getResult());
        nodes = tree.getDocuments().getDocuments().getItems();
        for (LawNodeView node : nodes)
            assertNotEquals(node.getDocType(), LawDocumentType.SECTION.name());
    }

    @Test
    public void getLawDocumentTest() {
        loadTestData(ETP.name(), true);
        ViewObjectResponse<?> genericDocument = (ViewObjectResponse<?>)testCtrl.getLawDocument(ETP.name(), "2", null, null);
        LawDocWithRefsView doc = ((LawDocWithRefsView) genericDocument.getResult());
        assertEquals(1, doc.getParentLocationIds().size());
        assertEquals("1974", doc.getParentLocationIds().get(0));
        assertEquals("1", doc.getPrevSibling().getLocationId());
        assertEquals("3", doc.getNextSibling().getLocationId());
    }

    @Test
    public void getLawDocumentWithRefTreeDateTest() {
        loadTestData(ABC.name(), true);
        for (String fileId : TEST_UPDATE_FILE_PREFIX)
            loadTestData(fileId, false);
        String initialDate = "2014-09-22";
        String[] locIds = {"A1", "1", "2", "3", "A2", "10", "11"};
        for (String locId : locIds) {
            ViewObjectResponse<?> genericDocument = (ViewObjectResponse<?>)testCtrl.getLawDocument(ABC.name(), locId, null, initialDate);
            LawDocWithRefsView doc = ((LawDocWithRefsView) genericDocument.getResult());
            assertNotNull(doc.getParents());
        }
    }

    @Test
    public void getRepealedLawsTest() {
        loadTestData(ABC.name(), true);
        for (String fileId : TEST_UPDATE_FILE_PREFIX)
            loadTestData(fileId, false);
        LocalDate defaultStartDate = LocalDate.of(1970, 1, 1);
        LocalDate testDate = LocalDate.of(2020, 5, 30);
        LocalDate defaultEndDate = LocalDate.now();
        BiMap<LocalDate, String> dateToLocId = HashBiMap.create();
        dateToLocId.put(testDate.minusDays(1), "2");
        dateToLocId.put(testDate, "3");
        dateToLocId.put(testDate.plusDays(1), "10");
        testRepealedResult(defaultStartDate, defaultEndDate, dateToLocId, 3);
        testRepealedResult(testDate, defaultEndDate, dateToLocId, 2);
        testRepealedResult(defaultStartDate, testDate, dateToLocId, 2);
        testRepealedResult(testDate, testDate, dateToLocId, 1);
    }

    private void testRepealedResult(LocalDate start, LocalDate end, BiMap<LocalDate, String> dateToLocId, int expectedSize) {
        Optional<LocalDate> expectedPublishDate = dateToLocId.keySet().stream().max(LocalDate::compareTo);
        assertTrue(expectedPublishDate.isPresent());
        List<RepealedLawDocIdView> repealedLaws = testCtrl.getRepealedLaws(start.toString(), end.toString()).getResult().getItems();
        assertEquals(expectedSize, repealedLaws.size());
        for (RepealedLawDocIdView curr : repealedLaws) {
            assertEquals(ABC.name(), curr.getLawId());
            LocalDate currRepealedDate = curr.getRepealedDate();
            String currLocId = curr.getLocationId();
            assertEquals(dateToLocId.get(currRepealedDate), currLocId);
            assertEquals(dateToLocId.inverse().get(currLocId), currRepealedDate);
            assertEquals(expectedPublishDate.get(), curr.getPublishedDate());
        }
    }

    @Test
    public void handleLawTreeNotFoundExTest() {
        String badLawId = "XXX";
        String date = "2020-06-28";
        try {
            testCtrl.getLawTree(badLawId, date, null, null, false);
        }
        catch (LawTreeNotFoundEx ex) {
            ViewObjectErrorResponse response = (ViewObjectErrorResponse) testCtrl.handleLawTreeNotFoundEx(ex);
            assertEquals(ErrorCode.LAW_TREE_NOT_FOUND.getCode(), response.getErrorCode());
            LawIdQueryView view = (LawIdQueryView) response.getErrorData();
            assertEquals(badLawId, view.getLawId());
            assertEquals(date, view.getEndDate());
        }
    }

    @Test
    public void handleLawDocNotFoundExTes() {
        loadTestData(TEST_LAW_IDS.get(0), true);
        String badDocId = TEST_LAW_IDS.get(0) + "0";
        String date = "2020-06-30";
        try {
            testCtrl.getLawDocument(badDocId.substring(0, 3), badDocId.substring(3), date, null);
        }
        catch (LawDocumentNotFoundEx ex) {
            ViewObjectErrorResponse response = (ViewObjectErrorResponse) testCtrl.handleLawDocNotFoundEx(ex);
            assertEquals(ErrorCode.LAW_DOC_NOT_FOUND.getCode(), response.getErrorCode());
            LawDocQueryView view = (LawDocQueryView) response.getErrorData();
            assertEquals(badDocId, view.getLawDocId());
            assertEquals(date, view.getEndDate());
        }
    }
}
