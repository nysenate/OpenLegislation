package gov.nysenate.openleg.processors;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.util.AsyncUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.processors.law.LawProcessService;
import gov.nysenate.openleg.processors.log.*;
import gov.nysenate.openleg.processors.transcripts.hearing.HearingProcessService;
import gov.nysenate.openleg.processors.transcripts.session.TranscriptProcessService;
import gov.nysenate.openleg.spotchecks.base.BaseSpotcheckProcessService;
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
import java.util.stream.Collectors;

/**
 * Process all the things.
 */
@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    @Autowired private OpenLegEnvironment env;
    @Autowired private EventBus eventBus;
    @Autowired private DataProcessLogService processLogService;
    @Autowired private AsyncUtils asyncUtils;

    @Autowired private LegDataProcessService legDataProcessService;

    @Autowired private TranscriptProcessService transcriptProcessService;
    @Autowired private HearingProcessService hearingProcessService;
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
            .add(hearingProcessService)
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
        if (!env.isProcessingEnabled()) {
            logger.debug("Data processing is disabled!");
            return null;
        }

        logger.info("Starting data processor...");
        currentRun = processLogService.startNewRun(LocalDateTime.now(), invoker);
        if (async)
            asyncUtils.run(this::doRun);
        else
            doRun();
        return currentRun;
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
            // Scheduled methods cannot let checked exceptions through.
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

    protected synchronized void collateAll() throws IOException {
        processAll(true);
    }

    protected synchronized void ingestAll() throws IOException {
        processAll(false);
    }

    /**
     * Common code for collating and ingesting data.
     * @param isCollate marks which operation it is.
     */
    private synchronized void processAll(boolean isCollate) {
        if (!env.isProcessingEnabled()) {
            logger.info("Not {} data, processing is disabled.", isCollate ? "collating" : "ingesting");
            return;
        }
        logger.info("Begin {} data", isCollate ? "collating" : "ingesting");
        Map<String, Integer> processedCounts = new LinkedHashMap<>();
        for (ProcessService processor : processServices) {
            if (Thread.currentThread().isInterrupted())
                break;
            String type = isCollate ? processor.getCollateType() : processor.getIngestType();
            logger.info("{} " + type, isCollate ? "Collating" : "Ingesting");
            int count = isCollate ? processor.collate() : processor.ingest();
            if (count > 0)
                processedCounts.put(type, count);
        }
        if (processedCounts.isEmpty())
            logger.info("Nothing to {}", isCollate ? "collate" : "ingest");
        else {
            logger.info("Completed {}. Statistics:", isCollate ? "collation" : "ingestion");
            logger.info(processedCounts.entrySet().stream()
                    .map(pair -> "\t" + pair.getKey() + ": " + pair.getValue()).collect(Collectors.joining(", ")));
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
            collateAll();
            ingestAll();
        }
        catch (Exception ex) {
            eventBus.post(new DataProcessErrorEvent("Unexpected Processing Error", ex, currentRun.getProcessId()));
            logger.error("Unexpected Processing Error:\n{}", ExceptionUtils.getStackTrace(ex));
        }
        if (!Thread.currentThread().isInterrupted())
            processLogService.finishRun(currentRun);
        logger.info("Exiting data processor.");
    }
}
