package gov.nysenate.openleg.processors.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.processors.bill.sobi.SobiFile;
import gov.nysenate.openleg.processors.bill.xml.XmlFile;
import gov.nysenate.openleg.processors.sourcefile.SourceFileRefDao;
import gov.nysenate.openleg.processors.sourcefile.sobi.FsSobiDao;
import gov.nysenate.openleg.processors.sourcefile.xml.FsXmlDao;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Robert Bebber on 4/3/17.
 */
public class SqlSourceFileDaoTester extends BaseTests {

    @Autowired
    SourceFileRefDao sourceFileRefDao;

    @Autowired private FsSobiDao sobiDao;
    @Autowired private FsXmlDao xmlDao;

    @Autowired
    OpenLegEnvironment environment;

    public File preTestSetup(String sourceName, String archiveName, boolean makedir) throws IOException {
        File temp = new File(archiveName, sourceName);
        if (makedir) {
            temp.mkdirs();
        }
        temp.createNewFile();
        temp.deleteOnExit();
        return temp;
    }

    @Test
    public void testXmlFileArchive() throws IOException {
        String fileName = "2017-02-06-12.13.28.656756_LDSUMM_A04892.XML";
        String stagingDir = environment.getStagingDir() +"/xmls/";
        String archiveDir = environment.getArchiveDir() + "/xmls/2017/LDSUMM";
        File stagingFile = preTestSetup(fileName, stagingDir, false);
        File expectedArchiveFile = new File(archiveDir,fileName);
        File retrievedArchiveFile = null;
        try {
            XmlFile sourceFile = new XmlFile(stagingFile);
            xmlDao.archiveSourceFile(sourceFile);
            sourceFileRefDao.updateSourceFile(sourceFile);
            retrievedArchiveFile = sourceFileRefDao.getSourceFile(fileName).getFile();
            assertEquals("XML File Insert Retrieval",
                    expectedArchiveFile.getAbsolutePath(), retrievedArchiveFile.getAbsolutePath());
        } finally {
            cleanUpFiles(stagingFile, expectedArchiveFile, retrievedArchiveFile);
        }
    }

    @Test
    public void testSobiFileArchive() throws IOException {
        String fileName = "SOBI.D160000.T124521.TXT";
        String stagingDir = environment.getStagingDir() + "/sobis/";
        String archiveDir = environment.getArchiveDir() + "/sobis/2015";
        File stagingFile = preTestSetup(fileName, stagingDir, false);
        File expectedArchiveFile = new File(archiveDir,fileName);
        File retrievedArchiveFile = null;
        try {
            SobiFile sourceFile = new SobiFile(stagingFile);
            sobiDao.archiveSourceFile(sourceFile);
            sourceFileRefDao.updateSourceFile(sourceFile);
            retrievedArchiveFile = sourceFileRefDao.getSourceFile(fileName).getFile();
            assertEquals("Sobi File Insert Retrieval",
                    expectedArchiveFile.getAbsolutePath(), retrievedArchiveFile.getAbsolutePath());
        } finally {
            cleanUpFiles(stagingFile, expectedArchiveFile, retrievedArchiveFile);
        }
    }

    @Test
    public void testXmlFileDate() throws IOException {
        String fileName = "2017-02-06-12.13.28.656756_LDSUMM_A04892.XML";
        String archiveDir = "/data/openleg/archive/xmls/2017/LDSUMM";
        File archiveFile = preTestSetup(fileName, archiveDir, false);
        SourceFile retrievedArchiveFile = null;
        try {
            SourceFile sourceFile = new XmlFile(archiveFile);
            sourceFile.setArchived(true);
            sourceFileRefDao.updateSourceFile(sourceFile);
            retrievedArchiveFile = sourceFileRefDao.getSourceFile(fileName);
            assertEquals("Xml File Insert Retrieval",
                    sourceFile.getPublishedDateTime(), retrievedArchiveFile.getPublishedDateTime());
        } finally {
            cleanUpFiles(archiveFile, retrievedArchiveFile.getFile());
        }
    }

    @Test
    public void testSobiFileDate() throws IOException {
        String fileName = "SOBI.D160000.T124521.TXT";
        String archiveDir = environment.getArchiveDir() + "/sobis/2015";
        File archiveFile = preTestSetup(fileName, archiveDir, false);
        SourceFile retrievedArchiveFile = null;
        try {
            SourceFile sourceFile = new SobiFile(archiveFile);
            sourceFile.setArchived(true);
            sourceFileRefDao.updateSourceFile(sourceFile);
            retrievedArchiveFile = sourceFileRefDao.getSourceFile(fileName);
            assertEquals("Sobi File Insert Retrieval",
                    sourceFile.getPublishedDateTime(), retrievedArchiveFile.getPublishedDateTime());
        } finally {
            cleanUpFiles(archiveFile, retrievedArchiveFile.getFile());
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
