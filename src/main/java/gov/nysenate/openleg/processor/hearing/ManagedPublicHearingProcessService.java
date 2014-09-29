package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.hearing.PublicHearingFileDao;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

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
    public void processPublicHearingFiles(List<PublicHearingFile> publicHearingFiles) {
        for (PublicHearingFile file : publicHearingFiles) {
            try {
                logger.info("Processing PublicHearingFile: " + file.getFileName());
                publicHearingParser.process(file);
                file.setProcessedCount(file.getProcessedCount() + 1);
                file.setPendingProcessing(false);
                file.setProcessedDateTime(LocalDateTime.now());
                publicHearingFileDao.updatePublicHearingFile(file);
            }
            catch (IOException ex) {
                logger.error("Error reading from PublicHearingFile: " + file.getFileName(), ex);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void processPendingPublicHearingFiles() {
        List<PublicHearingFile> publicHearingFiles;
        do {
            publicHearingFiles = getPendingPublicHearingFiles(LimitOffset.FIFTY);
            processPublicHearingFiles(publicHearingFiles);
        }
        while (!publicHearingFiles.isEmpty());
    }

    /** {@inheritDoc} */
    @Override
    public void updatePendingProcessing(PublicHearingId publicHearingId, boolean pendingProcessing) {
        throw new NotImplementedException();
    }
}
