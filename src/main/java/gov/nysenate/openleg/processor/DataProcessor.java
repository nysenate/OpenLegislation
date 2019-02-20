package gov.nysenate.openleg.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.process.*;
import gov.nysenate.openleg.processor.base.ProcessService;
import gov.nysenate.openleg.processor.hearing.PublicHearingProcessService;
import gov.nysenate.openleg.processor.law.LawProcessService;
import gov.nysenate.openleg.processor.legdata.LegDataProcessService;
import gov.nysenate.openleg.processor.transcript.TranscriptProcessService;
import gov.nysenate.openleg.service.process.DataProcessLogService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import gov.nysenate.openleg.util.AsyncUtils;
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
import java.util.Optional;

/**
 * Process all the things.
 */
@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    @Autowired private Environment env;
    @Autowired private EventBus eventBus;
    @Autowired private DataProcessLogService processLogService;
    @Autowired private AsyncUtils asyncUtils;

    @Autowired private LegDataProcessService legDataProcessService;

    @Autowired private TranscriptProcessService transcriptProcessService;
    @Autowired private PublicHearingProcessService publicHearingProcessService;
    @Autowired private LawProcessService lawProcessService;

    @Autowired private List<BaseSpotcheckProcessService> spotcheckProcessServices;

    private List<ProcessService> processServices;

    /** Hold a reference to the current data process run instance for event-based logging purposes. */
    private volatile DataProcessRun currentRun;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        processServices = ImmutableList.<ProcessService>builder()
            .add(legDataProcessService)
            .add(transcriptProcessService)
            .add(publicHearingProcessService)
            .add(lawProcessService)
            .addAll(spotcheckProcessServices)
            .build();
    }

    /* --- Main Methods --- */

    /**
     * Simple entry point to process new data for all supported data types.
     *
     * @param invoker String - describes method of invocation
     * @param async boolean - if true, processing will occur asynchronously.
     *              The unfinished {@link DataProcessRun} will be returned.
     * @return {@link DataProcessRun}
     * @throws Exception - If unhandled exceptions occur during processing
     */
    public synchronized DataProcessRun run(String invoker, boolean async) throws Exception {
        if (env.isProcessingEnabled()) {
            logger.info("Starting data processor...");
            currentRun = processLogService.startNewRun(LocalDateTime.now(), invoker);

            if (async) {
                asyncUtils.run(this::doRun);
            } else {
                doRun();
            }

            return currentRun;
        }
        else {
            logger.debug("Data processing is disabled!");
            return null;
        }
    }

    /**
     * Overload of {@link #run(String, boolean)} that defaults to not process asynchronously
     *
     * @param invoker String - describes method of invocation
     * @return {@link DataProcessRun}
     * @throws Exception - If unhandled exceptions occur during processing
     */
    public synchronized DataProcessRun run(String invoker) throws Exception {
        return run(invoker, false);
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

    /* --- Event Handlers --- */

    @Subscribe
    public void handleDataProcessErrorEvent(DataProcessErrorEvent ev) {
        if (currentRun != null) {
            currentRun.addException(ev.getMessage(), ev.getEx());
        }
    }

    @Subscribe
    public void handleDataProcessUnitEvent(DataProcessUnitEvent ev) {
        if (currentRun != null) {
            DataProcessUnit unit = ev.getUnit();
            processLogService.addUnit(currentRun.getProcessId(), unit);
            if (!unit.getErrors().isEmpty()) {
                eventBus.post(new DataProcessWarnEvent(currentRun.getProcessId(), unit));
            }
        }
    }

    /* --- Processing methods --- */

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

    public Optional<DataProcessRun> getCurrentRun() {
        return Optional.ofNullable(currentRun);
    }

    /* --- Internal Methods --- */

    /**
     * Performs a data process run, recording any errors to the current {@link DataProcessRun}
     */
    private synchronized void doRun() {
        try {
            collate();
            ingest();
        }
        catch (Exception ex) {
            eventBus.post(new DataProcessErrorEvent("Unexpected Processing Error", ex, currentRun.getProcessId()));
            logger.error("Unexpected Processing Error:\n{}", ExceptionUtils.getStackTrace(ex));
        }
        processLogService.finishRun(currentRun);
        logger.info("Exiting data processor.");
    }

    private void logCounts(Map<String, Integer> counts) {
        counts.forEach((type, count) -> logger.info("{}: {}", type, count));
    }
}
