package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.AbstractTranscriptsFile;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A file containing the raw Public Hearing text.
 */
public class PublicHearingFile extends AbstractTranscriptsFile {

    public PublicHearingFile(File file) throws FileNotFoundException {
        super(file);
    }
}
