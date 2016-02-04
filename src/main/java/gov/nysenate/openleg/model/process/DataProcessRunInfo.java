package gov.nysenate.openleg.model.process;

import java.util.Optional;

public class DataProcessRunInfo
{
    protected DataProcessRun run;
    protected Optional<DataProcessUnit> firstProcessed;
    protected Optional<DataProcessUnit> lastProcessed;

    public DataProcessRunInfo(DataProcessRun run) {
        this.run = run;
        this.firstProcessed = Optional.empty();
        this.lastProcessed = Optional.empty();
    }

    /** --- Basic Getters/Setters --- */

    public DataProcessRun getRun() {
        return run;
    }

    public void setRun(DataProcessRun run) {
        this.run = run;
    }

    public Optional<DataProcessUnit> getFirstProcessed() {
        return firstProcessed;
    }

    public void setFirstProcessed(Optional<DataProcessUnit> firstProcessed) {
        this.firstProcessed = firstProcessed;
    }

    public Optional<DataProcessUnit> getLastProcessed() {
        return lastProcessed;
    }

    public void setLastProcessed(Optional<DataProcessUnit> lastProcessed) {
        this.lastProcessed = lastProcessed;
    }
}
