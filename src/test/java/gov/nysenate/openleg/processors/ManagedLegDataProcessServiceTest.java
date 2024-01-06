package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import gov.nysenate.openleg.processors.bill.SourceFile;
import gov.nysenate.openleg.processors.bill.sobi.SobiFile;
import gov.nysenate.openleg.processors.bill.xml.XmlFile;
import gov.nysenate.openleg.processors.sourcefile.SourceFileRefDao;
import gov.nysenate.openleg.processors.sourcefile.sobi.LegDataFragmentDao;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Robert Bebber on 4/24/17.
 */
public class ManagedLegDataProcessServiceTest extends BaseTests {
    // get sobi/xml collate properly
    // make dummy sobi/xml file (cleaned)
    // run process service
    // pull unprocessed sourcefile from sobifragmentsdao
    // verify the files with the type fragments blocks
    //(Bill && one of the top group[xml/sobi] && test one of the new types[xml])
    //pull unprocessed sobifragments set to processed( method::getPending setProcessed in sobifragdao)

    @Autowired
    LegDataFragmentDao legDataFragmentDao;
    @Autowired
    ManagedLegDataProcessService managedSobiProcessService;
    @Autowired
    OpenLegEnvironment environment;
    @Autowired
    SourceFileRefDao sourceFileRefDao;

    private static final Logger logger = LoggerFactory.getLogger(ManagedLegDataProcessServiceTest.class);


    public File preTestSetup(String sourceName, File original, String destDirName) throws IOException {
        List<LegDataFragment> legDataFragments = legDataFragmentDao.getPendingLegDataFragments(SortOrder.NONE, LimitOffset.ALL);
        for (LegDataFragment legDataFragment : legDataFragments) {
            legDataFragment.setPendingProcessing(false);
        }
        File temp = new File(destDirName, sourceName);

        /*
            Creating a dummy file of the contents to imitate the original
        */
        temp.createNewFile();
        temp.deleteOnExit();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(original);
            out = new FileOutputStream(temp);
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            in.close();
            out.close();
        }
        return temp;
    }

    @Test
    public void ldsummCollateTest() throws IOException {
        String fileName = "2017-01-01-01.00.01.000000_LDSUMM_A00001.XML";
        File original = new File("/home/senateuser/IdeaProjects/OpenLegislation/src/test/resources/sourcefile/2017-02-06-12.13.28.656756_LDSUMM_A04892.XML");
        String stagingDir = environment.getStagingDir() + "/xmls/";
        String archiveDir = environment.getArchiveDir() + "/xmls/2017/LDSUMM";
        File stagingFile = preTestSetup(fileName, original, stagingDir);
        File expectedArchiveFile = new File(archiveDir, fileName);
        try {
            SourceFile sourceFile = new XmlFile(stagingFile);
            sourceFileRefDao.updateSourceFile(sourceFile);
            managedSobiProcessService.collateSourceFiles();
            List<LegDataFragment> legDataFragments = legDataFragmentDao.getLegDataFragments(sourceFile.getFileName(), SortOrder.ASC);
            for (LegDataFragment legDataFragment : legDataFragments) {
                assertEquals("Ldsumm Collade", LegDataFragmentType.LDSUMM, legDataFragment.getType());
            }
        } finally {
            cleanUpFiles(stagingFile, expectedArchiveFile);
        }
    }

    @Test
    public void agendaFragmentTest() throws IOException {
        String fileName = "SOBI.D170101.T000001.TXT";
        File original = new File("/home/senateuser/IdeaProjects/OpenLegislation/src/test/resources/sourcefile/SOBI.D170101.T000001.TXT");
        String stagingDir = environment.getStagingDir() + "/sobis/";
        String archiveDir = environment.getArchiveDir() + "/sobis/2017/";
        File stagingFile = preTestSetup(fileName, original, stagingDir);
        File expectedArchiveFile = new File(archiveDir, fileName);
        try {
            SourceFile sourceFile = new SobiFile(stagingFile);
            sourceFileRefDao.updateSourceFile(sourceFile);
            managedSobiProcessService.collateSourceFiles();
            List<LegDataFragment> legDataFragments = legDataFragmentDao.getLegDataFragments(sourceFile.getFileName(), SortOrder.ASC);
            assertEquals("Bill Fragment", LegDataFragmentType.BILL, legDataFragments.get(0).getType());
            assertEquals("AGENDA Fragment", LegDataFragmentType.AGENDA, legDataFragments.get(1).getType());
        } finally {
            cleanUpFiles(stagingFile, expectedArchiveFile);
        }
    }

    @Test
    public void calendarFragmentTest() throws IOException {
        String fileName = "SOBI.D160101.T000001.TXT";
        File original = new File("/home/senateuser/IdeaProjects/OpenLegislation/src/test/resources/sourcefile/SOBI.D160101.T000001.TXT");
        String stagingDir = environment.getStagingDir() + "/sobis/";
        String archiveDir = environment.getArchiveDir() + "/sobis/2016";
        File stagingFile = preTestSetup(fileName, original, stagingDir);
        File expectedArchiveFile = new File(archiveDir, fileName);
        try {
            SourceFile sourceFile = new SobiFile(stagingFile);
            sourceFileRefDao.updateSourceFile(sourceFile);
            managedSobiProcessService.collateSourceFiles();
            List<LegDataFragment> legDataFragments = legDataFragmentDao.getLegDataFragments(sourceFile.getFileName(), SortOrder.ASC);
            assertEquals("Calendar Fragment", LegDataFragmentType.CALENDAR_ACTIVE, legDataFragments.get(0).getType());
        } finally {
            cleanUpFiles(stagingFile, expectedArchiveFile);
        }
    }

    private void cleanUpFiles(File... files) {
        boolean allDeleted = Arrays.stream(files)
                .filter(File::exists)
                .map(FileUtils::deleteQuietly)
                .reduce(true, Boolean::logicalAnd);
        assertTrue("All Files non-existant / deleted", allDeleted);
    }
}
