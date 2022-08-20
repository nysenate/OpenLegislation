package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class ManagedTranscriptProcessService implements TranscriptProcessService {
    private static final Logger logger = LoggerFactory.getLogger(ManagedTranscriptProcessService.class);
    private final TranscriptFileDao transcriptFileDao;
    private final TranscriptDataService transcriptDataService;

    @Autowired
    public ManagedTranscriptProcessService(TranscriptFileDao transcriptFileDao, TranscriptDataService transcriptDataService) {
        this.transcriptFileDao = transcriptFileDao;
        this.transcriptDataService = transcriptDataService;
    }


    /* --- Implemented Methods --- */

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
                    transcriptFileDao.archiveTranscriptFile(file);
                    transcriptFileDao.updateTranscriptFile(file);
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
    public int processTranscriptFiles(List<TranscriptFile> transcriptFiles) {
        SortedMap<TranscriptFile, Transcript> processed = new TreeMap<>();
        int processCount = 0;
        for (TranscriptFile file : transcriptFiles) {
            try {
                logger.info("Processing transcript file {}", file.getFileName());
                processed.put(file, TranscriptParser.process(file));
                file.markAsProcessed();
                transcriptFileDao.updateTranscriptFile(file);
                processCount++;
            }
            catch (IOException ex) {
                logger.error("Error processing TranscriptFile " + file.getFileName() + ".", ex);
            }
        }
        logger.debug("Saving {} processed transcripts", processCount);
        for (var transcript : processed.values())
            transcriptDataService.saveTranscript(transcript, true);
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public int processPendingTranscriptFiles() {
        List<TranscriptFile> transcriptFiles;
        int processCount = 0;
        do {
            transcriptFiles = transcriptFileDao.getPendingTranscriptFiles(LimitOffset.FIFTY);
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
