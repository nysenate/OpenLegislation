package gov.nysenate.openleg.controller.api;

import gov.nysenate.openleg.dao.law.data.LawFileDao;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.processor.law.ManagedLawProcessService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gov.nysenate.openleg.model.law.LawChapterCode.*;

public class LawCtrlTest extends ApiTest {
    @Autowired
    private ManagedLawProcessService testService;
    @Autowired
    private LawFileDao testDao;

    protected static final String TEST_FILE_PREFIX = "src/test/resources/lawFiles/";
    protected static final List<String> TEST_LAW_IDS = Arrays.asList(EHC.name(), ETP.name(), CMA.name(), CMS.name(), ABC.name());
    protected static final List<String> TEST_UPDATE_FILE_PREFIX = Arrays.asList("20200529", "20200530", "20200531");

    protected void loadTestData(String fileId, boolean isInitial) {
        LawFile file;
        if (isInitial)
            file = new LawFile(new File(TEST_FILE_PREFIX + "DATABASE.LAW." + fileId));
        else
            file = new LawFile(new File(TEST_FILE_PREFIX + fileId + ".UPDATE"));
        testDao.updateLawFile(file);
        testService.processLawFiles(Collections.singletonList(file));
    }
}
