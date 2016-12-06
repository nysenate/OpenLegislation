package gov.nysenate.openleg.processor.xml;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.xml.XmlDao;
import gov.nysenate.openleg.model.process.DataProcessAction;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import gov.nysenate.openleg.model.process.DataProcessUnitEvent;
import gov.nysenate.openleg.model.xml.XmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.google.common.collect.ImmutableMap.builder;

/**
 * Implementation of XmlProcessService to process incoming xml files.
 */
@Service
public class ManagedXmlProcessService implements XmlProcessService {

    private static final Logger logger = LoggerFactory.getLogger(ManagedXmlProcessService.class);

    @Autowired
    private XmlDao xmlDao;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private Environment env;

    /**
     * --- Processor Dependencies ---
     */
    @Autowired
    private BillTextXmlProcessor billTextXmlProcessor;
    @Autowired
    private DigestSummaryXmlProcessor digestSummaryXmlProcessor;

    /**
     * Map the XmlFileTypes to the corresponding XmlProcessors
     */
    private ImmutableMap<XmlFileType, XmlProcessor> processorMap;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
        processorMap = ImmutableMap.<XmlFileType, XmlProcessor>builder()
                .put(XmlFileType.BILL_TEXT, billTextXmlProcessor)
                .put(XmlFileType.DIGEST_SUMMARY, digestSummaryXmlProcessor)
                .build();
    }

    /** --- Implemented Methods --- */

    @Override
    public int collate() {
        try {
            int totalCollated = 0;
            List<XmlFile> newXmls;
            // Iterate through all the new xml files in small batches to avoid saturating memory.
            do {
                newXmls = xmlDao.getIncomingXmlFiles(SortOrder.ASC, new LimitOffset(env.getXmlBatchSize()));
                logger.debug(newXmls.isEmpty() ? "No more xml files to collate." : "Collating {} xml files.", new XmlFiles.size());
                for (XmlFile xmlFile : newXmls) {
                    DataProcessUnit unit =
                            new DataProcessUnit("XML-FILE", xmlFile.getFileName(), LocalDateTime.now(), DataProcessAction.COLLATE);
                    // Record the xml file in the backing store.
                    xmlDao.updateXmlFile(xmlFile);
                    // Save the xml file and mark them as pending processing.
                    logger.info("Saving file {}", xmlFile.getFileId());
                    xmlFile.setPendingProcessing(true);
                    unit.addMessage("Saved " + xmlFile.getFileId());
                    // Now archive the xml file
                    xmlDao.archiveAndUpdateXmlFile(xmlFile);
                    totalCollated++;
                    unit.setEndDateTime(LocalDate.now());
                    eventBus.post(new DataProcessUnitEvent(unit));
                }
            }
            while (!newXmls.isEmpty() && env.isProcessingEnabled());
            return totalCollated;
        } catch (IOException ex) {
            String errorMessage = "Error encountered during collation of sobi files.";
            throw new DataIntegrityViolationException(errorMessage, ex);
        }
    }

    @Override
    public int ingest() {
        LimitOffset limitOffset = (env.isXmlBatchEnabled()) ? new LimitOffset(env.getXmlBatchSize()) : LimitOffset.ONE;
        return processXmlFiles(getPendingXmlFiles(SortOrder.ASC, limitOffset), XmlProcessOptions.builder().build());
    }

    @Override
    public string getCollateType() {
        return "xml file";
    }

    @Override String getIngestType() {
        return "xml file";
    }

    @Override
    public List<XmlFile> getPendingXmlFiles(SortOrder sortByPubDate, LimitOffset limitOffset) {
        return xmlDao.getPendingXmlFiles(sortByPubDate, limitOffset);
    }

    @Override
    public int processXmlFiles(List<XmlFile> files, XmlProcessOptions options) {
        logger.debug(files.isEmpty()) ? "No more files to process" : "Iterating through {} files", files.size());
        for (XmlFile file : files) {
            // Hand off processing to specific implementations based on file type.
            if (processorMap.containsKey(file.getType())) {
                processorMap.get(file.getType()).process(file);
            } else {
                logger.error("No processors have be registered to handle: " + file);
            }
            file.setProcessedCount(file.getProcessedCount() + 1);
            file.setProcessedDateTime(LocalDateTime.now());
        }
        // Perform any necessary post-processing/cleanup
        processorMap.values().forEach(p -> p.postProcess());
        // Set the files as processed and update
        file.forEach(f -> {
            f.setPendingProcessing(false);
            xmlDao.updateXmlFile(f);
        });
        return files.size();
    }

    @Override
    public void updatePendingProcessing(String fileId, boolean pendingProcessing) throws XmlFileNotFoundEx {
        try {
            XmlFile file = xmlDao.getXmlFile(fileId);
            file.setPendingProcessing(pendingProcessing);
            xmlDao.updateXmlFile(file);
        } catch (DataAccessException ex) {
            String errorMessage = "Error finding an xml file with the specified fildId";
            throw new XmlFragmentNotFoundEx(errorMessage, ex);
        }
    }

}
