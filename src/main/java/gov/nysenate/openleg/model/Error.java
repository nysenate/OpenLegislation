package gov.nysenate.openleg.model;

public class Error
{
private String billId;
private int errorId;
private int reportId;
private String lbdc;
private String json;
enum errorInfo{summary,title,action,sponsor,cosponsor};
private String errorInfo;



public String getBillId()
{
    return billId;
}
public void setBillId(String billId)
{
    this.billId = billId;
}
public int getErrorId()
{
    return errorId;
}
public void setErrorId(int errorId)
{
    this.errorId = errorId;
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
public String getErrorInfo()
{
    return errorInfo;
}
public void setErrorInfo(String errorInfo)
{
    this.errorInfo = errorInfo;
};

    
}
