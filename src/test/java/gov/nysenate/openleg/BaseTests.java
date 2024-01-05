package gov.nysenate.openleg;

import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.ConsoleApplicationConfig;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, ConsoleApplicationConfig.class})
@ActiveProfiles("test")
@Transactional
public abstract class BaseTests {
    @Autowired
    protected OpenLegEnvironment environment;

    @After
    public void deleteTestFiles() throws IOException {
        for (File dir : List.of(environment.getStagingDir(), environment.getArchiveDir())) {
            var files = FileIOUtils.safeListFiles(dir, true, null);
            for (File file : files) {
                FileUtils.forceDelete(file);
            }
        }
    }
}
