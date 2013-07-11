package gov.nysenate.openleg.model.admin;

public class ReportObservation
{
    private int id;
    private String oid;
    private String field;

    private int errorId;
    private ReportError error;
    private int reportId;
    private Report report;
    private String actualValue;
    private String observedValue;

    public ReportObservation() {

    }

    public ReportObservation(int reportId, String oid, String field, String actualValue, String observedValue) {
        this.reportId = reportId;
        this.oid = oid;
        this.field = field;
        this.actualValue = actualValue;
        this.observedValue = observedValue;
    }

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

    public int getErrorId()
    {
        return errorId;
    }

    public void setErrorId(int errorId)
    {
        this.errorId = errorId;
    }

    public ReportError getError()
    {
        return error;
    }

    public void setError(ReportError error)
    {
        this.error = error;
    }

    public Report getReport()
    {
        return report;
    }

    public void setReport(Report report)
    {
        this.report = report;
    }
}
