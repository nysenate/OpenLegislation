package gov.nysenate.openleg.model.sourcefiles.sobi;

/**
 * An exception that is raised when a sobi file has an invalid name that cannot be parsed
 */
public class InvalidSobiNameEx extends RuntimeException {
    private static final long serialVersionUID = 7009147657784905061L;

    private String fileName;

    public InvalidSobiNameEx(String fileName, Throwable cause) {
        super("Sobi file " + fileName + " does not conform to sobi file naming conventions", cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

