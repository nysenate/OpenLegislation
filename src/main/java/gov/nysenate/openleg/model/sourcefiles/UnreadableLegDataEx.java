package gov.nysenate.openleg.model.sourcefiles;

import gov.nysenate.openleg.model.sourcefiles.SourceFile;

/**
 * This exception is thrown when the contents of a source file cannot be read
 */
public class UnreadableLegDataEx extends RuntimeException {

    private static final long serialVersionUID = 8708541650408827491L;

    private SourceFile sourceFile;

    public UnreadableLegDataEx(SourceFile sourceFile, Throwable cause) {
        super("Could not read text from source file: " + sourceFile, cause);
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }
}
