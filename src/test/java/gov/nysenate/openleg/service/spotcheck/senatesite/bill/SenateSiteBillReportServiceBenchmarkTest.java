package gov.nysenate.openleg.service.spotcheck.senatesite.bill;

import com.google.common.base.Stopwatch;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckRunService;
import gov.nysenate.openleg.util.FileIOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.dao.bill.reference.senatesite.FsSenateSiteDao.SENSITE_DUMP_DIRNAME;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckRefType.SENATE_SITE_BILLS;

@Category(SillyTest.class)
public class SenateSiteBillReportServiceBenchmarkTest extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteBillReportServiceBenchmarkTest.class);

    @Autowired private SpotcheckRunService spotcheckRunService;
    @Autowired private SenateSiteBillReportService senateSiteBillReportService;
    @Autowired private Environment env;

    private int initialRefQueueSize;
    private int initialDataQueueSize;

    private static final String billDumpPath = SENSITE_DUMP_DIRNAME + "/" + SENATE_SITE_BILLS.getRefName();
    private static final Pattern testDumpRegex = Pattern.compile("senate-site-bills_dump-2017-20180226T050308.*");

    private static final int startingSize = 128;

    @Before
    public void setup() {
        initialRefQueueSize = env.getSensiteBillRefQueueSize();
        initialDataQueueSize = env.getSensiteBillDataQueueSize();
    }

    @After
    public void cleanup() {
        env.setSensiteBillRefQueueSize(initialRefQueueSize);
        env.setSensiteBillDataQueueSize(initialDataQueueSize);
    }

    @Test
    public void runReportTest() throws Exception {
        stageDump();
        spotcheckRunService.runReports(SENATE_SITE_BILLS);
    }

    @Test
    public void queueSizeBenchmarkTest() throws Exception {
        int queueSize = startingSize;
        TreeMap<Integer, Long> times = new TreeMap<>();
        try {
            while (true) {
                logger.info("Testing with queue size: {}", queueSize);
                Stopwatch sw = Stopwatch.createStarted();

                stageDump();
                env.setSensiteBillRefQueueSize(queueSize);
                env.setSensiteBillDataQueueSize(queueSize);
                senateSiteBillReportService.generateReport(null, null);

                long time = sw.elapsed(TimeUnit.SECONDS);
                logger.info("Finished test for queue size {}.  Took {}s", queueSize, time);
                times.put(queueSize, time);
                if (queueSize < 2) {
                    break;
                }
                queueSize = queueSize / 2;
            }
        } finally {
            logger.info("Benchmarking finished:");
            for (Map.Entry entry : times.entrySet()) {
                logger.info("size: {}\ttime: {}s", entry.getKey(), entry.getValue());
            }
        }
    }

    private void stageDump() throws IOException {
        File archiveDir = FileIOUtils.safeGetFolder(env.getArchiveDir(), billDumpPath);
        File stagingDir = FileIOUtils.safeGetFolder(env.getStagingDir(), billDumpPath);
        Collection<File> dumpFiles = FileUtils.listFiles(archiveDir, new RegexFileFilter(testDumpRegex), null);
        for (File file : dumpFiles) {
            FileIOUtils.moveFileToDirectory(file, stagingDir, false);
        }
    }

}