package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.util.FileIOUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.fail;

public abstract class AbstractProcessServiceTest extends BaseTests {
    private static final String STAGING_STR = "/data/openleg/staging/",
            TEST_STR = "src/test/resources/",
            ARCHIVE_STR = "/data/openleg/archive/";
    private final File archiveDir = new File(ARCHIVE_STR, getName());

    protected abstract ProcessService getProcessService();

    protected abstract String getName();

    /**
     * Since testing could be interrupted, we need a surefire way to clean out test files.
     * @param file to check.
     * @return if the archived file is a testing file.
     */
    protected abstract boolean isTestFile(File file);

    protected void processFiles(String... filenames) {
        File stagingDir = new File(STAGING_STR, getName());
        try {
            if (!FileIOUtils.safeListFiles(stagingDir, false, null).isEmpty())
                fail("Staging directory should be empty.");
            File testDir = new File(TEST_STR, getName());
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

    @After
    public void deleteTestFiles() throws IOException {
        List<File> testFiles = FileIOUtils.safeListFiles(archiveDir, false, null)
                .stream().filter(this::isTestFile).toList();
        for (var file : testFiles) {
            if(!FileUtils.deleteQuietly(file))
                fail("File " + file + " could not be deleted from the archive. May need to manually delete.");
        }
    }
}
