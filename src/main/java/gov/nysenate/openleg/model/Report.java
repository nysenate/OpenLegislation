package gov.nysenate.openleg.model;

import java.util.Date;

public class Report
{
    private int reportId;
    private Date date;

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
}
