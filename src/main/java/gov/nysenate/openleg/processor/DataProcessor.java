package gov.nysenate.openleg.processor;

import gov.nysenate.openleg.model.sobi.SobiProcessOptions;
import gov.nysenate.openleg.processor.base.SobiProcessService;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    /** --- Process Services --- */

    @Autowired
    private SobiProcessService sobiProcessService;

    @Autowired
    private DaybreakProcessService daybreakProcessService;

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
        daybreakProcessService.collateDaybreakReports();
        // TODO: Collate Transcripts / Public Hearings
        // TODO: Collate Laws Of NY
        // TODO: Handle CMS.TEXT (Rules file)
    }

    public void ingest() throws IOException {
        sobiProcessService.processPendingFragments(SobiProcessOptions.builder().build());
        daybreakProcessService.processPendingFragments();
        // TODO: Process Transcripts / Public Hearings
        // TODO: Process Laws of NY
    }
}