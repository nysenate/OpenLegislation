package gov.nysenate.openleg.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.base.ProcessService;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import gov.nysenate.openleg.processor.hearing.PublicHearingProcessService;
import gov.nysenate.openleg.processor.law.LawProcessService;
import gov.nysenate.openleg.processor.sobi.SobiProcessService;
import gov.nysenate.openleg.processor.transcript.TranscriptProcessService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    /** --- Process Services --- */

    @Autowired private SobiProcessService sobiProcessService;
    @Autowired private DaybreakProcessService daybreakProcessService;
    @Autowired private TranscriptProcessService transcriptProcessService;
    @Autowired private PublicHearingProcessService publicHearingProcessService;
    @Autowired private LawProcessService lawProcessService;

    private List<ProcessService> processServices;

    /** --- Events --- */

    @Autowired
    private EventBus eventBus;

    @Autowired
    Environment environment;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        processServices = ImmutableList.<ProcessService>builder()
                .add(sobiProcessService)
                .add(daybreakProcessService)
                .add(transcriptProcessService)
                .add(publicHearingProcessService)
                .add(lawProcessService)
                .build();
    }

    /** --- Main Method --- */

    /**
     * Simple entry point to process new data for all supported data types.
     * @throws Exception
     */
    public void run() throws Exception {
        logger.info("Starting data processor...");
        collate();
        ingest();
        logger.info("Exiting data processor.");
    }


    @Scheduled(cron = "${scheduler.process.cron}")
    public void scheduledRun() {
        if (environment.isProcessingScheduled()) {
            try {
                run();
            } catch (Exception ex) {
                logger.error("Caught exception while processing data\n{}", ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    /** --- Processing methods --- */

    public void collate() {
        logger.info("Begin collating data");
        Map<String, Integer> collatedCounts = new LinkedHashMap<>();
        for (ProcessService processor : processServices) {
            int collatedCount = processor.collate();
            if (collatedCount > 0) {
                collatedCounts.put(processor.getCollateType(), collatedCount);
            }
        }
        if (collatedCounts.size() > 0) {
            logger.info("Completed collations:");
            logCounts(collatedCounts);
        } else {
            logger.info("Nothing to collate");
        }
    }

    public void ingest() throws IOException {
        logger.info("Begin ingesting data");
        Map<String, Integer> ingestedCounts = new LinkedHashMap<>();
        for (ProcessService processor : processServices) {
            int ingestedCount = processor.ingest();
            if (ingestedCount > 0) {
                ingestedCounts.put(processor.getIngestType(), ingestedCount);
            }
        }
        if (ingestedCounts.size() > 0) {
            logger.info("Completed ingestion:");
            logCounts(ingestedCounts);
        } else {
            logger.info("Nothing to ingest");
        }
    }

    private void logCounts(Map<String, Integer> counts) {
        counts.forEach((type, count) -> logger.info("{}: {}", type, count));
    }
}
