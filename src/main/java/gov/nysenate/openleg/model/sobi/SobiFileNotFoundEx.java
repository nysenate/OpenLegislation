package gov.nysenate.openleg.model.sobi;

public class SobiFileNotFoundEx extends RuntimeException
{
    private final String sobiFileName;

    public SobiFileNotFoundEx(String sobiFileName) {
        super("SobiFile with the filename: " + sobiFileName + " was not found.");
        this.sobiFileName = sobiFileName;
    }

    public String getSobiFileName() {
        return sobiFileName;
    }
}
