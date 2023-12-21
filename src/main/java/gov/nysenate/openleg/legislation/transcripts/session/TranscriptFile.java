package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.AbstractTranscriptsFile;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * File containing the raw transcript text
 */
public class TranscriptFile extends AbstractTranscriptsFile {
    public TranscriptFile(File file) throws FileNotFoundException {
        super(file);
    }
}
