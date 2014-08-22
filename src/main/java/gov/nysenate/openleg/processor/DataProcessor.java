package gov.nysenate.openleg.processor;

import gov.nysenate.openleg.model.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.base.SobiProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    /** --- Process Services --- */

    @Autowired
    private SobiProcessService sobiProcessService;

    /** --- Main --- */

    public void run() throws Exception {
        logger.info("Starting data processor...");
        collate();
        ingest();
        logger.info("Exiting data processor.");
    }

    /** --- Processing methods --- */

    public void collate() {
        sobiProcessService.collateSobiFiles();
        // TODO: Collate Transcripts / Public Hearings
        // TODO: Collate Laws Of NY
        // TODO: Collate Daybreak files
        // TODO: Handle CMS.TEXT (Rules file)
    }

    public void ingest() throws IOException {
        sobiProcessService.processPendingFragments(SobiProcessOptions.builder().build());
        // TODO: Process Transcripts / Public Hearings
        // TODO: Process Laws of NY
        // TODO: Process Daybreak files, run SpotCheck Reports
    }
}