package gov.nysenate.openleg.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.process.DataProcessErrorEvent;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.process.DataProcessUnitEvent;
import gov.nysenate.openleg.processor.base.ProcessService;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import gov.nysenate.openleg.processor.hearing.PublicHearingProcessService;
import gov.nysenate.openleg.processor.law.LawProcessService;
import gov.nysenate.openleg.processor.sobi.SobiProcessService;
import gov.nysenate.openleg.processor.transcript.TranscriptProcessService;
import gov.nysenate.openleg.service.process.DataProcessLogService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    @Autowired private Environment env;
    @Autowired private EventBus eventBus;
    @Autowired private DataProcessLogService processLogService;

    @Autowired private SobiProcessService sobiProcessService;
    @Autowired private TranscriptProcessService transcriptProcessService;
    @Autowired private PublicHearingProcessService publicHearingProcessService;
    @Autowired private LawProcessService lawProcessService;

    private List<ProcessService> processServices;

    /** Hold a reference to the current data process run instance for event-based logging purposes. */
    private DataProcessRun currentRun;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        processServices = ImmutableList.<ProcessService>builder()
            .add(sobiProcessService)
            .add(transcriptProcessService)
            .add(publicHearingProcessService)
            .add(lawProcessService)
            .build();
    }

    /** --- Main Methods --- */

    /**
     * Simple entry point to process new data for all supported data types.
     * @throws Exception
     */
    public synchronized void run(String invoker) throws Exception {
        if (env.isProcessingEnabled()) {
            logger.info("Starting data processor...");
            currentRun = processLogService.startNewRun(LocalDateTime.now(), invoker);
            try {
                collate();
                ingest();
            }
            catch (Exception ex) {
                String err = "Unexpected processing error! " + ex.getMessage();
                logger.error(err, ex);
                currentRun.addException(err);
            }
            processLogService.finishRun(currentRun);
            logger.info("Exiting data processor.");
        }
        else {
            logger.debug("Data processing is disabled!");
        }
    }

    /**
     * If scheduled processing is enabled, the #run method will be invoked according to the
     * configured cron value.
     */
    @Scheduled(cron = "${scheduler.process.cron}")
    public synchronized void scheduledRun() {
        if (env.isProcessingScheduled()) {
            try {
                run("Scheduler");
            }
            // Scheduled methods cannot let checked exceptions through
            catch (Exception ex) {
                logger.error("Caught exception while processing data\n{}", ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    /** --- Event Handlers --- */

    @Subscribe
    public void handleDataProcessErrorEvent(DataProcessErrorEvent ev) {
        if (currentRun != null) {
            currentRun.addException(ev.getMessage());
        }
    }

    @Subscribe
    public void handleDataProcessUnitEvent(DataProcessUnitEvent ev) {
        if (currentRun != null) {
            processLogService.addUnit(currentRun.getProcessId(), ev.getUnit());
        }
    }

    /** --- Processing methods --- */

    public synchronized void collate() {
        logger.debug("Begin collating data");
        Map<String, Integer> collatedCounts = new LinkedHashMap<>();
        for (ProcessService processor : processServices) {
            if (env.isProcessingEnabled()) {
                int collatedCount = processor.collate();
                if (collatedCount > 0) {
                    collatedCounts.put(processor.getCollateType(), collatedCount);
                }
            }

        }
        if (collatedCounts.size() > 0) {
            logger.debug("Completed collations:");
            logCounts(collatedCounts);
        }
        else {
            logger.info("Nothing to collate");
        }
    }

    public synchronized void ingest() throws IOException {
        logger.debug("Begin ingesting data");
        Map<String, Integer> ingestedCounts = new LinkedHashMap<>();
        for (ProcessService processor : processServices) {
            if (env.isProcessingEnabled()) {
                int ingestedCount = processor.ingest();
                if (ingestedCount > 0) {
                    ingestedCounts.put(processor.getIngestType(), ingestedCount);
                }
            }
        }
        if (ingestedCounts.size() > 0) {
            logger.debug("Completed ingestion:");
            logCounts(ingestedCounts);
        }
        else {
            logger.info("Nothing to ingest");
        }
    }

    private void logCounts(Map<String, Integer> counts) {
        counts.forEach((type, count) -> logger.info("{}: {}", type, count));
    }
}
