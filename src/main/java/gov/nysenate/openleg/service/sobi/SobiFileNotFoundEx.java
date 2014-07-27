package gov.nysenate.openleg.service.sobi;

public class SobiFileNotFoundEx extends Exception
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
