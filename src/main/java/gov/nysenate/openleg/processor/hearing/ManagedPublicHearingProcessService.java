package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.hearing.PublicHearingFileDao;
import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ManagedPublicHearingProcessService implements PublicHearingProcessService
{
    private static Logger logger = LoggerFactory.getLogger(ManagedPublicHearingProcessService.class);

    @Autowired
    private PublicHearingFileDao publicHearingFileDao;

    @Autowired
    private PublicHearingParser publicHearingParser;

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public int collate() {
        return collatePublicHearingFiles();
    }

    /** {@inheritDoc} */
    @Override
    public int ingest() {
        return processPendingPublicHearingFiles();
    }

    /** {@inheritDoc} */
    @Override
    public String getCollateType() {
        return "public hearing file";
    }

    /** {@inheritDoc} */
    @Override
    public int collatePublicHearingFiles() {
        int numCollated = 0;
        try {
            List<PublicHearingFile> publicHearingFiles;
            do {
                publicHearingFiles = publicHearingFileDao.getIncomingPublicHearingFiles(LimitOffset.FIFTY);
                for (PublicHearingFile file : publicHearingFiles) {
                    file.setPendingProcessing(true);
                    file.setArchived(false);
                    publicHearingFileDao.updatePublicHearingFile(file);
                    publicHearingFileDao.archivePublicHearingFile(file);
                    numCollated++;
                }
            }
            while (!publicHearingFiles.isEmpty());
        }
        catch (IOException ex) {
            logger.error("Error retrieving public hearing files during collation.", ex);
        }

        return numCollated;
    }

    /** {@inheritDoc} */
    @Override
    public List<PublicHearingFile> getPendingPublicHearingFiles(LimitOffset limitOffset) {
        return publicHearingFileDao.getPendingPublicHearingFile(limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public int processPublicHearingFiles(List<PublicHearingFile> publicHearingFiles) {
        int processCount = 0;
        for (PublicHearingFile file : publicHearingFiles) {
            try {
                logger.info("Processing PublicHearingFile: " + file.getFileName());
                publicHearingParser.process(file);
                file.setProcessedCount(file.getProcessedCount() + 1);
                file.setPendingProcessing(false);
                file.setProcessedDateTime(LocalDateTime.now());
                publicHearingFileDao.updatePublicHearingFile(file);
                processCount++;
            }
            catch (IOException ex) {
                logger.error("Error reading from PublicHearingFile: " + file.getFileName(), ex);
            }
        }
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public int processPendingPublicHearingFiles() {
        List<PublicHearingFile> publicHearingFiles;
        int processCount = 0;
        do {
            publicHearingFiles = getPendingPublicHearingFiles(LimitOffset.FIFTY);
            processCount += processPublicHearingFiles(publicHearingFiles);
        }
        while (!publicHearingFiles.isEmpty());
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public void updatePendingProcessing(PublicHearingId publicHearingId, boolean pendingProcessing) {
        throw new UnsupportedOperationException();
    }
}
