package gov.nysenate.openleg.util;

import java.util.Date;


public class Change
{
    private Storage.Status status;
    private Date date;
    
    public Change(Storage.Status status)
    {
        this.status = status;
    }
    
    public Change(Storage.Status status, Date date)
    {
        this.status = status;
        this.date = date;
    }
    
    public Storage.Status getStatus()
    {
        return status;
    }

    public void setStatus(Storage.Status status)
    {
        this.status = status;
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
