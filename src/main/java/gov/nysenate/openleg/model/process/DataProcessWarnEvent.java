package gov.nysenate.openleg.model.process;

public class DataProcessWarnEvent {

    int dataProcessId;
    DataProcessUnit unit;

    public DataProcessWarnEvent(int dataProcessId, DataProcessUnit unit) {
        this.dataProcessId = dataProcessId;
        this.unit = unit;
    }

    public int getDataProcessId() {
        return dataProcessId;
    }

    public DataProcessUnit getUnit() {
        return unit;
    }
}
