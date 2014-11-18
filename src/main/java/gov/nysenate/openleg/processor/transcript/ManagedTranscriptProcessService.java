package gov.nysenate.openleg.processor.transcript;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.transcript.TranscriptFileDao;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ManagedTranscriptProcessService implements TranscriptProcessService
{
    private static Logger logger = LoggerFactory.getLogger(ManagedTranscriptProcessService.class);

    @Autowired
    private TranscriptFileDao transcriptFileDao;

    @Autowired
    private TranscriptParser transcriptParser;


    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public int collate() {
        return collateTranscriptFiles();
    }

    /** {@inheritDoc} */
    @Override
    public int ingest() {
        return processPendingTranscriptFiles();
    }

    @Override
    public String getCollateType() {
        return "transcript file";
    }

    /** {@inheritDoc} */
    @Override
    public int collateTranscriptFiles() {
        logger.debug("Collating transcript files...");
        int numCollated = 0;
        try {
            List<TranscriptFile> transcriptFiles;
            do {
                transcriptFiles = transcriptFileDao.getIncomingTranscriptFiles(LimitOffset.FIFTY);
                for (TranscriptFile file : transcriptFiles) {
                    file.setPendingProcessing(true);
                    transcriptFileDao.archiveAndUpdateTranscriptFile(file);
                    numCollated++;
                }
            }
            while (!transcriptFiles.isEmpty());
        }
        catch (IOException ex) {
            logger.error("Error retrieving transcript files during collation", ex);
        }
        logger.debug("Collated {} transcript files.", numCollated);
        return numCollated;
    }

    /** {@inheritDoc} */
    @Override
    public List<TranscriptFile> getPendingTranscriptFiles(LimitOffset limitOffset) {
        return transcriptFileDao.getPendingTranscriptFiles(limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public int processTranscriptFiles(List<TranscriptFile> transcriptFiles) {
        int processCount = 0;
        for (TranscriptFile file : transcriptFiles) {
            try {
                logger.info("Processing transcript file {}", file.getFileName());
                transcriptParser.process(file);
                file.setProcessedCount(file.getProcessedCount() + 1);
                file.setPendingProcessing(false);
                file.setProcessedDateTime(LocalDateTime.now());
                transcriptFileDao.updateTranscriptFile(file);
                processCount++;
            }
            catch (IOException ex) {
                logger.error("Error processing TranscriptFile " + file.getFileName() + ".", ex);
            }
        }
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public int processPendingTranscriptFiles() {
        List<TranscriptFile> transcriptFiles;
        int processCount = 0;
        do {
            transcriptFiles = getPendingTranscriptFiles(LimitOffset.FIFTY);
            processCount += processTranscriptFiles(transcriptFiles);
        }
        while (!transcriptFiles.isEmpty());
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public void updatePendingProcessing(TranscriptId transcriptId, boolean pendingProcessing) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
