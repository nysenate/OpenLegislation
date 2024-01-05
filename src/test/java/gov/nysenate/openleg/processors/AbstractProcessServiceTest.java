package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import org.apache.commons.io.FileUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public abstract class AbstractProcessServiceTest extends BaseTests {
    private static final String TEST_STR = "src/test/resources/";
    private File stagingDir, testDir;

    @PostConstruct
    private void init() {
        stagingDir = new File(environment.getStagingDir(), processDirName());
        testDir = new File(TEST_STR, testFileLocation());
    }

    protected abstract ProcessService getProcessService();

    protected abstract String processDirName();

    protected abstract String testFileLocation();

    /**
     * Since testing could be interrupted, we need a surefire way to clean out test files.
     * @param file to check.
     * @return if the archived file is a testing file.
     */
    protected abstract boolean isTestFile(File file);

    protected void processFiles(String... filenames) {
        try {
            // Move files into the staging directory, so they can be processed.
            for (var filename : filenames) {
                var testFile = new File(testDir, filename);
                if (!isTestFile(testFile))
                    fail("File " + testFile + " must be marked as a test file!");
                else
                    FileUtils.copyFile(testFile, new File(stagingDir, filename));
            }
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        getProcessService().collate();
        getProcessService().ingest();
    }
}
