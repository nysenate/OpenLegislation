package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.AbstractTranscriptsFile;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A file containing the raw Hearing text.
 */
public class HearingFile extends AbstractTranscriptsFile {
    public HearingFile(File file) throws FileNotFoundException {
        super(file);
    }
}
