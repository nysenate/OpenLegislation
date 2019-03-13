package gov.nysenate.openleg.model.sourcefiles;

/**
 * An exception that is raised when a sobi file has an invalid name that cannot be parsed
 */
public class InvalidLegDataFileNameEx extends RuntimeException {
    private static final long serialVersionUID = 7009147657784905061L;

    private String fileName;

    public InvalidLegDataFileNameEx(String fileName, Throwable cause) {
        super("Leg data file " + fileName + " does not conform to leg data file naming conventions", cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

