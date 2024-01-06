package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class ManagedHearingProcessService implements HearingProcessService {
    private static final Logger logger = LoggerFactory.getLogger(ManagedHearingProcessService.class);
    private final HearingFileDao hearingFileDao;
    private final HearingDataService hearingDataService;

    @Autowired
    public ManagedHearingProcessService(HearingFileDao hearingFileDao,
                                        HearingDataService hearingDataService) {
        this.hearingFileDao = hearingFileDao;
        this.hearingDataService = hearingDataService;
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public int collate() {
        return collateHearingFiles();
    }

    /** {@inheritDoc} */
    @Override
    public int ingest() {
        return processHearingFiles();
    }

    /** {@inheritDoc} */
    @Override
    public String getCollateType() {
        return "hearing file";
    }

    /** {@inheritDoc} */
    @Override
    public int collateHearingFiles() {
        int numCollated = 0;
        try {
            List<HearingFile> hearingFiles;
            do {
                hearingFiles = hearingFileDao.getIncomingFiles();
                for (HearingFile file : hearingFiles) {
                    file.setPendingProcessing(true);
                    hearingFileDao.archiveFile(file);
                    hearingFileDao.updateFile(file);
                    numCollated++;
                }
            }
            while (!hearingFiles.isEmpty());
        }
        catch (IOException ex) {
            logger.error("Error retrieving hearing files during collation.", ex);
        }
        logger.debug("Collated {} hearing files.", numCollated);
        return numCollated;
    }

    /** {@inheritDoc} */
    @Override
    public int processHearingFiles(List<HearingFile> hearingFiles) {
        SortedMap<HearingFile, Hearing> processed = new TreeMap<>();
        int processCount = 0;
        for (HearingFile file : hearingFiles) {
            try {
                logger.info("Processing hearing file {}", file.getFileName());
                processed.put(file, HearingParser.process(file));
                file.markAsProcessed();
                hearingFileDao.updateFile(file);
                processCount++;
            }
            catch (IOException ex) {
                logger.error("Error reading from HearingFile: " + file.getFileName(), ex);
            }
        }
        logger.debug("Saving {} hearings", processCount);
        for (var hearing : processed.values())
            hearingDataService.saveHearing(hearing, true);
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public int processHearingFiles() {
        List<HearingFile> hearingFiles;
        int processCount = 0;
        do {
            hearingFiles = hearingFileDao.getPendingFiles();
            processCount += processHearingFiles(hearingFiles);
        }
        while (!hearingFiles.isEmpty());
        return processCount;
    }

    /** {@inheritDoc} */
    @Override
    public void updatePendingProcessing(HearingId hearingId, boolean pendingProcessing) {
        throw new UnsupportedOperationException();
    }
}
