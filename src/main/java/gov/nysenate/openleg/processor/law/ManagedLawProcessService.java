package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.law.data.LawFileDao;
import gov.nysenate.openleg.model.law.LawFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ManagedLawProcessService implements LawProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(ManagedLawProcessService.class);

    @Autowired private Environment env;
    @Autowired private LawFileDao lawFileDao;
    @Autowired private LawProcessor lawProcessor;

    /** {@inheritDoc}*/
    @Override
    public int collate() {
        return collateLawFiles();
    }

    /** {@inheritDoc} */
    @Override
    public int ingest() {
        return processPendingLawFiles();
    }

    /** {@inheritDoc} */
    @Override
    public String getCollateType() {
        return "law file";
    }

    /** {@inheritDoc} */
    @Override
    public int collateLawFiles() {
        int numCollated = 0;
        try {
            List<LawFile> lawFiles = lawFileDao.getIncomingLawFiles(SortOrder.ASC, LimitOffset.ALL);
            for (LawFile lf : lawFiles) {
                lf.setPendingProcessing(true);
                lawFileDao.archiveAndUpdateLawFile(lf);
                numCollated++;
            }
        }
        catch (IOException ex) {
            logger.error("Failed to retrieve incoming laws from the file system.", ex);
        }
        logger.debug("Collated {} law files.", numCollated);
        return numCollated;
    }

    /** {@inheritDoc} */
    @Override
    public List<LawFile> getPendingLawFiles(LimitOffset limitOffset) {
        return lawFileDao.getPendingLawFiles(SortOrder.ASC, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public void processLawFiles(List<LawFile> lawFiles) {
        for (LawFile lawFile : lawFiles) {
            if (!env.isProcessingEnabled()) break;
            // Process the law file
            lawProcessor.process(lawFile);
            lawFile.setProcessedCount(lawFile.getProcessedCount() + 1);
            lawFile.setPendingProcessing(false);
            lawFile.setProcessedDateTime(LocalDateTime.now());
            lawFileDao.updateLawFile(lawFile);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int processPendingLawFiles() {
        List<LawFile> lawFiles = getPendingLawFiles(LimitOffset.ALL);
        processLawFiles(lawFiles);
        return lawFiles.size();
    }
}
