package gov.nysenate.openleg.model.process;

public class DataProcessUnitEvent
{
    private DataProcessUnit unit;

    public DataProcessUnitEvent(DataProcessUnit unit) {
        this.unit = unit;
    }

    public DataProcessUnit getUnit() {
        return unit;
    }
}
