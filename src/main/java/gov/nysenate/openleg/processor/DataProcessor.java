package gov.nysenate.openleg.processor;

import gov.nysenate.openleg.model.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import gov.nysenate.openleg.processor.hearing.PublicHearingProcessService;
import gov.nysenate.openleg.processor.law.LawProcessService;
import gov.nysenate.openleg.processor.sobi.SobiProcessService;
import gov.nysenate.openleg.processor.transcript.TranscriptProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    /** --- Process Services --- */

    @Autowired
    private SobiProcessService sobiProcessService;

    @Autowired
    private DaybreakProcessService daybreakProcessService;

    @Autowired
    private TranscriptProcessService transcriptProcessService;

    @Autowired
    private PublicHearingProcessService publicHearingProcessService;

    @Autowired
    private LawProcessService lawProcessService;

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

    /** --- Processing methods --- */

    public void collate() {
        logger.info("Begin collating data");
        sobiProcessService.collateSobiFiles();
        daybreakProcessService.collateDaybreakReports();
        transcriptProcessService.collateTranscriptFiles();
        publicHearingProcessService.collatePublicHearingFiles();
        lawProcessService.collateLawFiles();
        // TODO: Handle CMS.TEXT (Rules file)
        logger.info("Completed collations.");
    }

    public void ingest() throws IOException {
        logger.info("Being ingesting data");
        sobiProcessService.processPendingFragments(SobiProcessOptions.builder().build());
        daybreakProcessService.processPendingFragments();
        transcriptProcessService.processPendingTranscriptFiles();
        publicHearingProcessService.processPendingPublicHearingFiles();
        lawProcessService.processPendingLawFiles();
        logger.info("Completed ingest.");
    }
}
