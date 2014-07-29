package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.service.sobi.SobiProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class DataProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    /** --- Data Services --- */

    @Autowired
    private SobiProcessService sobiProcessService;

    /** --- Processor Instances --- */

    @PostConstruct
    public void init() {}

    /** --- Processing methods --- */

    public void collate() {
        sobiProcessService.collateSobiFiles();
        // TODO: Collate Transcripts / Public Hearings
        // TODO: Collate Laws Of NY
        // TODO: Handle CMS.TEXT (Rules file)
    }

    public void ingest() throws IOException {
//        sobiProcessService.processPendingFragments();
        // TODO: Process Transcripts / Public Hearings
    }
}
