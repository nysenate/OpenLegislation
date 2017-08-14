package gov.nysenate.openleg.processor.sobi;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileRefDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.SobiFragmentDao;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
@Transactional
public class xmlIngestProcessingIT extends BaseTests {
    @Autowired
    private SobiProcessService processService;

    @Autowired
    private Environment env;

    @Autowired
    SourceFileRefDao sourceFileRefDao;

    @Autowired
    SobiFragmentDao sobiFragmentDao;

    private File xmlIncoming;

    private File xmlArchive;

    private static final List<String> fileNames = Arrays.asList(
            "2017-01-01-00.00.00.000000_BILLSTAT_S00009.XML",
            "2017-02-02-00.00.00.000000_BILLSTAT_S0009A.XML"
    );

    private final File testFileDir = new File(
            getClass().getClassLoader().getResource("sourcefile/").getFile());


    @PostConstruct
    public void init() {
        File incomingDir = env.getStagingDir();
        xmlIncoming = new File(incomingDir, "xmls");
        File archiveDir = env.getArchiveDir();
        xmlArchive = new File(archiveDir, "xmls");
    }

    @Before
    public void stageXMLFiles() throws IOException {
        for (String fileName : fileNames) {
            File file = new File(testFileDir, fileName);
            FileUtils.copyFileToDirectory(file, xmlIncoming);
        }
    }

    @After
    public void cleanUpFiles() {
        List<File> potentialFiles = fileNames.stream()
                .map(fileName -> Arrays.asList(
                        new File(xmlIncoming, fileName),
                        new File(xmlArchive, fileName)
                ))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        boolean allDeleted = potentialFiles.stream()
                .filter(File::exists)
                .map(FileUtils::deleteQuietly)
                .reduce(true, Boolean::logicalAnd);
        assertTrue("All Files non-existant / deleted", allDeleted);
    }

    @Test
    public void collateIngestTest() throws FileNotFoundException {
        int collatedFileCount = processService.collate();
        assertEquals("Files were collated", fileNames.size(), collatedFileCount);
        fileNames.forEach(this::verifyXMLFragmentDates);

        //ingest documents now
        int ingestedFileCcount = processService.ingest();
        assertEquals("Files were ingested", fileNames.size(), ingestedFileCcount);
        fileNames.forEach(this::verifyXMLFragmentDates);
    }

    /* --- Internal Methods --- */

    private void verifyXMLFragmentDates(String fileName) {
        File file = new File(testFileDir, fileName);
        SourceFile sourceFile = sourceFileRefDao.getSourceFile(fileName);
        List<SobiFragment> xmlFragments = sobiFragmentDao.getSobiFragments(sourceFile, SortOrder.NONE);
        assertEquals("File has one fragment", 1, xmlFragments.size());

        xmlFragments.get(0).getPublishedDateTime(); //Should be the original date
    }

}
