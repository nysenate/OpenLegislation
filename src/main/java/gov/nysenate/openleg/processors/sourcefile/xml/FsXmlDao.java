package gov.nysenate.openleg.processors.sourcefile.xml;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.processors.bill.SourceType;
import gov.nysenate.openleg.processors.bill.xml.XmlFile;
import gov.nysenate.openleg.processors.sourcefile.SourceFileFsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.common.util.FileIOUtils.getSortedFiles;

/**
 * Created by Robert Bebber on 4/12/17.
 */
@Repository
public class FsXmlDao implements SourceFileFsDao<XmlFile> {

    /**
     * Reference to the environment in which the data is stored
     */
    @Autowired
    protected OpenLegEnvironment environment;
    private File incomingSourceDir;
    private File archiveSourceDir;
    private static final Pattern xmlType = Pattern.compile("(?:_)(\\w+)(?:_)");

    /**
     * This method sets up the directories of the incoming and archiving files.
     */
    @PostConstruct
    protected void init() {
        incomingSourceDir = new File(environment.getStagingDir(), "xmls");
        archiveSourceDir = new File(environment.getArchiveDir(), "xmls");
    }

    @Override
    public SourceType getSourceType() {
        return SourceType.XML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<XmlFile> getIncomingSourceFiles(SortOrder sortByFileName,
                                               LimitOffset limitOffset) throws IOException {
        List<File> files = new ArrayList<>(getSortedFiles(incomingSourceDir, false, null));
        if (sortByFileName == SortOrder.DESC) {
            Collections.reverse(files);
        }
        files = LimitOffset.limitList(files, limitOffset);
        List<XmlFile> xmlFile = new ArrayList<>();
        for (File file : files) {
            xmlFile.add(toXmlFile(file));
        }
        return xmlFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void archiveSourceFile(XmlFile xmlFile) throws IOException {
        File stageFile = xmlFile.getFile();
        // Archive the file only if the current one is residing in the incoming xmls directory.
        if (stageFile.getParentFile().compareTo(incomingSourceDir) == 0) {
            File archiveFile = getFileInArchiveDir(xmlFile.getFileName(),
                    xmlFile.getPublishedDateTime());
            FileIOUtils.moveFile(stageFile, archiveFile);
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

    /* --- Internal Methods --- */

    private XmlFile toXmlFile(File file) throws IOException {
        if (file.getName().contains("_SENAGEN_")) {
            return new XmlFile(file, "CP1252");
        }
        return new XmlFile(file);
    }
}
