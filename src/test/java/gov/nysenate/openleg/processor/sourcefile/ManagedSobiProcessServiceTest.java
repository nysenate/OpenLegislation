package gov.nysenate.openleg.processor.sourcefile;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileRefDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.SobiFragmentDao;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;
import gov.nysenate.openleg.processor.sobi.ManagedSobiProcessService;
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
public class ManagedSobiProcessServiceTest extends BaseTests {
    // get sobi/xml collate properly
    // make dummy sobi/xml file (cleaned)
    // run process service
    // pull unprocessed sourcefile from sobifragmentsdao
    // verify the files with the type fragments blocks
    //(Bill && one of the top group[xml/sobi] && test one of the new types[xml])
    //pull unprocessed sobifragments set to processed( method::getPending setProcessed in sobifragdao)

    @Autowired
    SobiFragmentDao sobiFragmentDao;
    @Autowired
    ManagedSobiProcessService managedSobiProcessService;
    @Autowired
    Environment environment;
    @Autowired
    SourceFileRefDao sourceFileRefDao;

    private static final Logger logger = LoggerFactory.getLogger(ManagedSobiProcessServiceTest.class);


    public File preTestSetup(String sourceName, File original, String destDirName) throws IOException {
        List<SobiFragment> sobiFragments = sobiFragmentDao.getPendingSobiFragments(SortOrder.NONE, LimitOffset.ALL);
        for (SobiFragment sobiFragment : sobiFragments) {
            sobiFragment.setPendingProcessing(false);
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
            List<SobiFragment> sobiFragments = sobiFragmentDao.getSobiFragments(sourceFile, SortOrder.ASC);
            for (SobiFragment sobiFragment : sobiFragments) {
                assertEquals("Ldsumm Collade", SobiFragmentType.LDSUMM, sobiFragment.getType());
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
            List<SobiFragment> sobiFragments = sobiFragmentDao.getSobiFragments(sourceFile, SortOrder.ASC);
            assertEquals("Bill Fragment", SobiFragmentType.BILL, sobiFragments.get(0).getType());
            assertEquals("AGENDA Fragment", SobiFragmentType.AGENDA, sobiFragments.get(1).getType());
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
            List<SobiFragment> sobiFragments = sobiFragmentDao.getSobiFragments(sourceFile, SortOrder.ASC);
            assertEquals("Calendar Fragment", SobiFragmentType.CALENDAR_ACTIVE, sobiFragments.get(0).getType());
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
