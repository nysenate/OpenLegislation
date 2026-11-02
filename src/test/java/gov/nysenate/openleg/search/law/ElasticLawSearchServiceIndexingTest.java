package gov.nysenate.openleg.search.law;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.law.*;
import gov.nysenate.openleg.legislation.law.dao.LawDataService;
import gov.nysenate.openleg.legislation.law.dao.LawTreeNotFoundEx;
import gov.nysenate.openleg.updates.law.BulkLawUpdateEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ElasticLawSearchServiceIndexingTest {
    private static final LocalDate PUBLISHED = LocalDate.of(2025, 1, 3);

    private ElasticLawSearchDao lawSearchDao;
    private LawDataService lawData;
    private ElasticLawSearchService lawSearch;

    @Before
    public void setUp() {
        lawSearchDao = mock(ElasticLawSearchDao.class);
        lawData = mock(LawDataService.class);
        lawSearch = new ElasticLawSearchService(lawSearchDao, lawData, new EventBus());
        OpenLegEnvironment env = mock(OpenLegEnvironment.class);
        when(env.isElasticIndexing()).thenReturn(true);
        ReflectionTestUtils.setField(lawSearch, "env", env);
    }

    @Test
    public void rebuildLoadsEachChapterTreeOnce() {
        when(lawData.getLawInfos()).thenReturn(List.of(lawInfo("XYZ"), lawInfo("ABC")));
        when(lawData.getLawDocuments(eq("ABC"), any()))
                .thenReturn(byId(doc("ABC", "1"), doc("ABC", "2"), doc("ABC", "3")));
        when(lawData.getLawDocuments(eq("XYZ"), any())).thenReturn(byId(doc("XYZ", "1"), doc("XYZ", "2")));
        when(lawData.getLawTree("ABC")).thenReturn(tree("ABC", "1", "2"));
        when(lawData.getLawTree("XYZ")).thenReturn(tree("XYZ", "1", "2"));

        lawSearch.rebuildIndex();

        verify(lawData, times(1)).getLawTree("ABC");
        verify(lawData, times(1)).getLawTree("XYZ");
        InOrder inOrder = inOrder(lawSearchDao);
        inOrder.verify(lawSearchDao).updateIndex(List.of(doc("ABC", "1"), doc("ABC", "2")));
        inOrder.verify(lawSearchDao).updateIndex(List.of(doc("XYZ", "1"), doc("XYZ", "2")));
        verifyIndexCalls(2);
    }

    @Test
    public void bulkUpdateMixingChaptersLoadsEachTreeOnceAndKeepsOrder() {
        when(lawData.getLawTree("ABC")).thenReturn(tree("ABC", "1", "2"));
        when(lawData.getLawTree("XYZ")).thenReturn(tree("XYZ", "1"));

        lawSearch.handleBulkLawUpdate(new BulkLawUpdateEvent(List.of(
                doc("ABC", "1"), doc("XYZ", "1"), doc("ABC", "3"), doc("XYZ", "2"), doc("ABC", "2"))));

        verify(lawData, times(1)).getLawTree("ABC");
        verify(lawData, times(1)).getLawTree("XYZ");
        verify(lawSearchDao).updateIndex(List.of(doc("ABC", "1"), doc("XYZ", "1"), doc("ABC", "2")));
        verifyIndexCalls(1);
    }

    @Test
    public void chapterWithoutTreeIsLeftOut() {
        when(lawData.getLawTree("ABC")).thenReturn(tree("ABC", "1", "2"));
        when(lawData.getLawTree("NOT")).thenThrow(new LawTreeNotFoundEx("NOT", null, "no tree"));

        lawSearch.handleBulkLawUpdate(new BulkLawUpdateEvent(List.of(
                doc("NOT", "1"), doc("ABC", "1"), doc("NOT", "2"), doc("ABC", "2"))));

        verify(lawData, times(1)).getLawTree("NOT");
        verify(lawSearchDao).updateIndex(List.of(doc("ABC", "1"), doc("ABC", "2")));
        verifyIndexCalls(1);
    }

    @Test
    public void nothingIsIndexedWhenNoDocumentIsInATree() {
        when(lawData.getLawTree("ABC")).thenReturn(tree("ABC", "1"));
        when(lawData.getLawTree("NOT")).thenThrow(new LawTreeNotFoundEx("NOT", null, "no tree"));

        lawSearch.handleBulkLawUpdate(new BulkLawUpdateEvent(List.of(doc("NOT", "1"), doc("ABC", "2"))));

        verifyNoInteractions(lawSearchDao);
    }

    /** No index requests reach Elasticsearch beyond the expected bulk ones. */
    private void verifyIndexCalls(int bulkCalls) {
        verify(lawSearchDao, times(bulkCalls)).updateIndex(anyCollection());
        verify(lawSearchDao, never()).updateIndex(any(LawDocument.class));
    }

    private static LawInfo lawInfo(String lawId) {
        LawInfo lawInfo = new LawInfo();
        lawInfo.setLawId(lawId);
        return lawInfo;
    }

    /** A current tree for the chapter, holding a section for each location ID. */
    private static LawTree tree(String lawId, String... locationIds) {
        LawTreeNode root = new LawTreeNode(docInfo(lawId, "-CH", LawDocumentType.CHAPTER), 1);
        for (int i = 0; i < locationIds.length; i++) {
            root.addChild(new LawTreeNode(docInfo(lawId, locationIds[i], LawDocumentType.SECTION), i + 2));
        }
        return new LawTree(new LawVersionId(lawId, PUBLISHED), root, lawInfo(lawId));
    }

    private static LawDocument doc(String lawId, String locationId) {
        return new LawDocument(docInfo(lawId, locationId, LawDocumentType.SECTION), "Text of " + locationId);
    }

    private static LawDocInfo docInfo(String lawId, String locationId, LawDocumentType type) {
        return new LawDocInfo(lawId + locationId, PUBLISHED, lawId, locationId, "Title " + locationId,
                type, locationId);
    }

    private static Map<String, LawDocument> byId(LawDocument... docs) {
        return Arrays.stream(docs).collect(Collectors.toMap(LawDocument::getDocumentId, Function.identity(),
                (a, b) -> a, LinkedHashMap::new));
    }
}
