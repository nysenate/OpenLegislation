package gov.nysenate.openleg.model;

public class Error
{
    public enum TYPE {summary,title,action,sponsor,cosponsor};

    private int id;
    private String errorType;
    private int reportId;
    private String billId;
    private String lbdc;
    private String json;

    public String getErrorType()
    {
        return errorType;
    }
    public void setErrorType(String errorType)
    {
        this.errorType = errorType;
    }

    public String getBillId()
    {
        return billId;
    }
    public void setBillId(String billId)
    {
        this.billId = billId;
    }

    public int getId()
    {
        return id;
    }
    public void setId(int errorId)
    {
        this.id = errorId;
    }

    public int getReportId()
    {
        return reportId;
    }
    public void setReportId(int reportId)
    {
        this.reportId = reportId;
    }

    public String getLbdc()
    {
        return lbdc;
    }
    public void setLbdc(String lbdc)
    {
        this.lbdc = lbdc;
    }

    public String getJson()
    {
        return json;
    }
    public void setJson(String json)
    {
        this.json = json;
    }
}
