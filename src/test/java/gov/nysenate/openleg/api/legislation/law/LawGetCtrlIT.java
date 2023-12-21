package gov.nysenate.openleg.api.legislation.law;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.api.legislation.law.view.*;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import gov.nysenate.openleg.legislation.law.dao.LawDocumentNotFoundEx;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class LawGetCtrlIT extends LawCtrlBaseIT {

    @Autowired
    private LawGetCtrl testCtrl;

    @Test
    public void getAllLawsTest() {
        loadTestData(true, TEST_LAW_IDS);
        ImmutableList<?> genericAllLaws = ((ListViewResponse<?>)testCtrl.getLaws(testRequest)).getResult().getItems();
        assertTrue(TEST_LAW_IDS.length <= genericAllLaws.size());
        List<String> lawIds = new ArrayList<>();
        for (Object o : genericAllLaws)
            lawIds.add(((LawInfoView)o).getLawId());
        for (String lawId : TEST_LAW_IDS)
            assertTrue(lawIds.contains(lawId));
    }

    @Test
    public void getLawTreeTest() {
        loadTestData(true, EHC.name());
        loadTestData(true, CMA.name());
        loadTestData(true, CMS.name());
        ViewObjectResponse<?> genericTree = (ViewObjectResponse<?>)testCtrl.getLawTree(EHC.name(), null, null, null, false);
        LawTreeView tree = ((LawTreeView) genericTree.getResult());
        ImmutableList<LawNodeView> nodes = tree.getDocuments().getDocuments().getItems();
        for (int i = 1; i <= 4; i++) {
            assertEquals(Integer.toString(i), nodes.get(i-1).getFromSection());
            assertEquals(Integer.toString(i), nodes.get(i-1).getToSection());
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
        loadTestData(true, ETP.name());
        ViewObjectResponse<?> genericDocument = (ViewObjectResponse<?>)testCtrl.getLawDocument(ETP.name(), "2", null, null);
        LawDocWithRefsView doc = ((LawDocWithRefsView) genericDocument.getResult());
        assertEquals(1, doc.getParentLocationIds().size());
        assertEquals("1974", doc.getParentLocationIds().get(0));
        assertEquals("1", doc.getPrevSibling().getLocationId());
        assertEquals("3", doc.getNextSibling().getLocationId());
    }

    @Test
    public void getLawDocumentWithRefTreeDateTest() {
        loadTestData(true, ABC.name());
        loadTestData(false, TEST_UPDATE_FILES);
        String initialDate = "2014-09-22";
        String[] locIds = {"A1", "4", "5", "A2", "6", "7"};
        for (String locId : locIds) {
            ViewObjectResponse<?> genericDocument = (ViewObjectResponse<?>)testCtrl.getLawDocument(ABC.name(), locId, null, initialDate);
            LawDocWithRefsView doc = ((LawDocWithRefsView) genericDocument.getResult());
            assertNotNull(doc.getParents());
        }
    }

    @Test
    public void getRepealedLawsTest() {
        loadTestData(true, ABC.name());
        loadTestData(false, TEST_UPDATE_FILES);

        LocalDate startDate = LocalDate.of(2014, 9, 22);
        LocalDate testDate = startDate.plusDays(2);
        LocalDate endDate = testDate.plusDays(2);
        BiMap<LocalDate, String> dateToLocId = HashBiMap.create();
        dateToLocId.put(testDate.minusDays(1), "4");
        dateToLocId.put(testDate, "5");
        dateToLocId.put(testDate.plusDays(1), "6");
        testRepealedResult(startDate, endDate, dateToLocId, 3);
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
        String lawId = ABC.name();
        loadTestData(true, lawId);
        String badDocId = lawId + "0";
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
