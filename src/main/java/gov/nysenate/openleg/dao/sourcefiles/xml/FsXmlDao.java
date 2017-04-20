package gov.nysenate.openleg.dao.sourcefiles.xml;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.sourcefiles.SqlSourceFileDao;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Robert Bebber on 4/12/17.
 */
@Repository
public class FsXmlDao implements XmlDao {

    @Autowired
    SqlSourceFileDao sqlSourceFileDao;
    /**
     * Reference to the environment in which the data is stored
     */
    @Autowired
    protected Environment environment;
    private File incomingSourceDir;
    private File archiveSourceDir;
    private Pattern xmlType = Pattern.compile("(?:_)(\\w+)(?:_)");

    /**
     * This method sets up the directories of the incoming and archiving files.
     */
    @PostConstruct
    protected void init() {
        incomingSourceDir = new File(environment.getStagingDir(), "xmls");
        archiveSourceDir = new File(environment.getArchiveDir(), "xmls");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlFile getFile(String fileName) throws DataAccessException {
        return (XmlFile) sqlSourceFileDao.getSourceFile(fileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void archiveXmlFile(SourceFile xmlFile) throws IOException {
        File stageFile = xmlFile.getFile();
        // Archive the file only if the current one is residing in the incoming xmls directory.
        if (stageFile.getParentFile().compareTo(incomingSourceDir) == 0) {
            File archiveFile = getFileInArchiveDir(xmlFile.getFileName(),
                    xmlFile.getPublishedDateTime());
            moveFile(stageFile, archiveFile);
            xmlFile.setFile(archiveFile);
            xmlFile.setArchived(true);
        } else {
            throw new FileNotFoundException("XmlFile " + stageFile + " must be in the incoming xmls directory in " +
                    "order to be archived.");
        }
    }

    /**
     * {@inheritDoc}
     */
    private void moveFile(File sourceFile, File destFile) throws IOException {
        if (destFile.exists()) {
            FileUtils.deleteQuietly(destFile);
        }
        FileUtils.moveFile(sourceFile, destFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFileInIncomingDir(String fileName) {
        return new File(incomingSourceDir, fileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime) {
        String year = Integer.toString(publishedDateTime.getYear());
        Matcher matt = xmlType.matcher(fileName);
        matt.find();
        File dir = new File(archiveSourceDir + "/" + year, matt.group(1));
        return new File(dir, fileName);
    }
}
