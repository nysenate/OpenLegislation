package gov.nysenate.openleg.processors.sourcefile.sobi;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.processors.bill.SourceType;
import gov.nysenate.openleg.processors.bill.sobi.SobiFile;
import gov.nysenate.openleg.processors.sourcefile.SourceFileFsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static gov.nysenate.openleg.common.util.FileIOUtils.getSortedFiles;
import static gov.nysenate.openleg.common.util.FileIOUtils.moveFile;

/**
 * Sobi files are stored in the file system to preserve their original formatting but metadata
 * for the files are stored in the database. The returned SobiFile instances are constructed
 * utilizing both data sources.
 */
@Repository
public class FsSobiDao implements SourceFileFsDao<SobiFile> {

    @Autowired
    OpenLegEnvironment environment;

    private static final Logger logger = LoggerFactory.getLogger(FsSobiDao.class);
    /**
     * Directory where new sobi files come in from external sources.
     */
    private File incomingSobiDir;

    /**
     * Directory where sobi files that have been processed are stored.
     */
    private File archiveSobiDir;

    @PostConstruct
    protected void init() {
        incomingSobiDir = new File(environment.getStagingDir(), "sobis");
        archiveSobiDir = new File(environment.getArchiveDir(), "sobis");
    }

    /** --- Implemented Methods --- */

    @Override
    public SourceType getSourceType() {
        return SourceType.SOBI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SobiFile> getIncomingSourceFiles(SortOrder sortByFileName,
                                                 LimitOffset limitOffset) throws IOException {
        List<File> files = new ArrayList<>(getSortedFiles(incomingSobiDir));
        if (sortByFileName == SortOrder.DESC) {
            Collections.reverse(files);
        }
        files = LimitOffset.limitList(files, limitOffset);
        List<SobiFile> sobiFiles = new ArrayList<>();
        for (File file : files) {
            sobiFiles.add(new SobiFile(file));
        }
        return sobiFiles;
    }

    /**
     * Method archives SobiFile. Moves SourceFile from staging to archive.
     *
     * @param sourceFile SourceFile(Sobi) to be archived
     * @throws IOException
     */
    @Override
    public void archiveSourceFile(SobiFile sourceFile) throws IOException {
        File stageFile = sourceFile.getFile();
        // Archive the file only if the current one is residing in the incoming sobis directory.
        if (stageFile.getParentFile().compareTo(incomingSobiDir) == 0) {
            File archiveFile = getFileInArchiveDir(sourceFile.getFileName(),
                    sourceFile.getPublishedDateTime());
            moveFile(stageFile, archiveFile);
            sourceFile.setFile(archiveFile);
            sourceFile.setArchived(true);
        } else {
            throw new FileNotFoundException("SobiFile " + stageFile + " must be in the incoming sobis directory in " +
                    "order to be archived.");
        }
    }

    /**
     * Get file handle from incoming sobi directory.
     */
    @Override
    public File getFileInIncomingDir(String fileName) {
        return new File(incomingSobiDir, fileName);
    }

    /**
     * Get file handle from the sobi archive directory.
     */
    @Override
    public File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime) {
        String year = Integer.toString(publishedDateTime.getYear());
        File dir = new File(archiveSobiDir, year);
        return new File(dir, fileName);
    }

}
