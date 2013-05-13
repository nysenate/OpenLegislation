package gov.nysenate.openleg.model;

import java.sql.Timestamp;
import java.util.Date;
public class Report
{
public int getReportId()
    {
        return reportId;
    }
    public void setReportId(int reportId)
    {
        this.reportId = reportId;
    }
    public Date getDate()
    {
        return date;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }
int reportId;
Date date;

    
    
}
