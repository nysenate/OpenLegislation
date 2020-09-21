package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.dao.law.data.LawFileDao;
import gov.nysenate.openleg.model.law.*;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.data.LawDocumentNotFoundEx;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class LawProcessorIT extends BaseTests {
    private static final String TEST_FILE_PREFIX = "src/test/resources/lawFiles/";
    private static final String INITIAL_PREFIX = "DATABASE.LAW.";
    private static final String UPDATE_SUFFIX = ".UPDATE";

    @Autowired
    private LawProcessor testProcessor;
    @Autowired
    private LawFileDao testDao;
    @Autowired
    private LawDataService lawDataService;

    @Test
    public void testProcess() {
        String code = LawChapterCode.CCO.name();
        String locId = "-CH77";
        loadFile(true, code);
        LawDocInfo info = lawDataService.getLawDocInfo(code + locId, null);
        assertEquals(LawChapterCode.CCO.getName(), info.getTitle());
        assertEquals(LawDocumentType.CHAPTER, info.getDocType());
        assertEquals(locId.substring(3), info.getDocTypeId());
        assertFalse(info.isDummy());

        LawDocument doc = lawDataService.getLawDocument(code + "1", null);
        String expectedText = "Section 1. Short title. This chapter shall be known as the\\n" +
                "\"cooperative corporations law.\"\\n";
        assertEquals(expectedText, doc.getText().trim());
        assertTrue(4 <= lawDataService.getLawInfos().size());
        // Tests an update.
        loadFile(false, "20200601");
        boolean errored = false;
        try {
            lawDataService.getLawDocInfo(code + "1", null);
        }
        catch (LawDocumentNotFoundEx e) {
            errored = true;
        }
        assertTrue(errored);
    }

    private void loadFile(boolean isInitial, String fileId) {
        String filename = TEST_FILE_PREFIX + (isInitial ? INITIAL_PREFIX + fileId : fileId + UPDATE_SUFFIX);
        LawFile testFile = new LawFile(new File(filename));
        testDao.updateLawFile(testFile);
        testProcessor.process(testFile);
    }
}
