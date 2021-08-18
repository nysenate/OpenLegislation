package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class ManagedPublicHearingProcessService implements PublicHearingProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(ManagedPublicHearingProcessService.class);

    @Autowired
    private PublicHearingFileDao publicHearingFileDao;

    @Autowired
    private PublicHearingDataService hearingDataService;

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
                    publicHearingFileDao.archivePublicHearingFile(file);
                    publicHearingFileDao.updatePublicHearingFile(file);
                    numCollated++;
                }
            }
            while (!publicHearingFiles.isEmpty());
        }
        catch (IOException ex) {
            logger.error("Error retrieving public hearing files during collation.", ex);
        }
        logger.debug("Collated {} hearing files.", numCollated);
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
        SortedSet<PublicHearing> parsedHearings = new TreeSet<>(Comparator.comparing(PublicHearing::getDate));
        int processCount = 0;
        for (PublicHearingFile file : publicHearingFiles) {
            try {
                logger.info("Processing public hearing file {}", file.getFileName());
                parsedHearings.add(PublicHearingParser.process(file));
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
        logger.info("Saving {} public hearings", processCount);
        for (var hearing : parsedHearings)
            hearingDataService.savePublicHearing(hearing, true);
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
