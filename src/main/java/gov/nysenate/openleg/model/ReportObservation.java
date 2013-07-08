package gov.nysenate.openleg.model;

public class ReportObservation
{
    private int id;
    private String oid;
    private String field;

    private int reportId;
    private String actualValue;
    private String observedValue;

    public int getId()
    {
        return id;
    }
    public void setId(int errorId)
    {
        this.id = errorId;
    }

    public String getOid()
    {
        return oid;
    }
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    public String getField()
    {
        return field;
    }
    public void setField(String field)
    {
        this.field = field;
    }

    public int getReportId()
    {
        return reportId;
    }
    public void setReportId(int reportId)
    {
        this.reportId = reportId;
    }

    public String getActualValue()
    {
        return actualValue;
    }
    public void setActualValue(String actualValue)
    {
        this.actualValue = actualValue;
    }

    public String getObservedValue()
    {
        return observedValue;
    }
    public void setObservedValue(String observedValue)
    {
        this.observedValue = observedValue;
    }
}
