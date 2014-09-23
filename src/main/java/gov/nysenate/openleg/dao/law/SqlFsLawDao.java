package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.law.LawFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gov.nysenate.openleg.util.FileIOUtils.getSortedFiles;

@Repository
public class SqlFsLawDao extends SqlBaseDao implements LawDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsLawDao.class);

    /** Directory where new law files come in. */
    private File incomingLawDir;

    /** Directory where law files that have been processed are stored. */
    private File archiveLawDir;

    @PostConstruct
    protected void init() {
        this.incomingLawDir = new File(environment.getStagingDirectory(), "laws");
        this.archiveLawDir = new File(environment.getArchiveDirectory(), "laws");
    }

    /** --- Implemented Methods --- */

    @Override
    public List<LawFile> getIncomingLawFiles(SortOrder sortByDate, LimitOffset limitOffset) throws IOException {
        List<File> files = new ArrayList<>(getSortedFiles(this.incomingLawDir, false, null));
        List<LawFile> lawFiles = new ArrayList<>();
        for (File file : files) {
            lawFiles.add(new LawFile(file));
        }
        if (sortByDate.equals(SortOrder.ASC)) {
            Collections.sort(lawFiles);
        }
        else {
            Collections.sort(lawFiles, Collections.reverseOrder());
        }
        lawFiles = LimitOffset.limitList(lawFiles, limitOffset);
        return lawFiles;
    }

    @Override
    public void updateLawFile(LawFile lawFile) throws IOException {
    }

    @Override
    public void archiveAndUpdateLawFile(LawFile lawFile) throws IOException {
        // Archive the file only if it's currently in the incoming directory.
        File file = lawFile.getFile();
        if (file.getParentFile().compareTo(this.incomingLawDir) == 0) {
            File archiveFile = getFileInArchiveDir(file.getName());
            moveFile(file, archiveFile);
            lawFile.setFile(archiveFile);
            lawFile.setArchived(true);
            updateLawFile(lawFile);
        }
        else {
            throw new FileNotFoundException(
                "The source law file must be in the incoming laws directory in order to be archived.");
        }
    }

    /** --- Internal Methods --- */

    /**
     * Get file handle from the sobi archive directory.
     */
    private File getFileInArchiveDir(String fileName) {
        File dir = new File(this.archiveLawDir, fileName);
        return new File(dir, fileName);
    }
}
