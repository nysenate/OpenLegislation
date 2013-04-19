package gov.nysenate.openleg.model;

import java.sql.Timestamp;
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
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }
int reportId;
Timestamp timestamp;
    
    
}
