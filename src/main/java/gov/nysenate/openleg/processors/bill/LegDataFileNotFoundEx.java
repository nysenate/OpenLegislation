package gov.nysenate.openleg.processors.bill;

public class LegDataFileNotFoundEx extends RuntimeException
{
    private final String legDataFileName;

    public LegDataFileNotFoundEx(String legDataFileName) {
        super("LegDataFile with the filename: " + legDataFileName + " was not found.");
        this.legDataFileName = legDataFileName;
    }

    public String getLegDataFileName() {
        return legDataFileName;
    }
}
