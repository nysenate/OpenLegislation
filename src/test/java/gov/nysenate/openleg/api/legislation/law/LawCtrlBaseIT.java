package gov.nysenate.openleg.api.legislation.law;

import gov.nysenate.openleg.api.ApiTest;
import gov.nysenate.openleg.legislation.law.dao.LawFileDao;
import gov.nysenate.openleg.processors.law.LawFile;
import gov.nysenate.openleg.processors.law.ManagedLawProcessService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;

public class LawCtrlBaseIT extends ApiTest {
    @Autowired
    private ManagedLawProcessService testService;
    @Autowired
    private LawFileDao testDao;

    protected static final String TEST_FILE_PREFIX = "src/test/resources/lawFiles/";
    protected static final List<String> TEST_LAW_IDS = Arrays.asList(ABC.name(), EHC.name(), ETP.name(), CMA.name(), CMS.name()),
        TEST_UPDATE_FILE_PREFIX = Arrays.asList("20140923", "20140924", "20140925");

    protected void loadTestData(String fileId, boolean isInitial) {
        String filename = isInitial ? "DATABASE.LAW." + fileId : fileId + ".UPDATE";
        LawFile file = new LawFile(new File(TEST_FILE_PREFIX + filename));
        testDao.updateLawFile(file);
        testService.processLawFiles(Collections.singletonList(file));
    }
}
