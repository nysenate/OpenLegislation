package gov.nysenate.openleg.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import gov.nysenate.openleg.util.Storage;

public class Change
{
    private Storage.Status status;
    private Date date;
    
    public Change(Storage.Status status)
    {
        this.status = status;
        this.date = new Date();
    }
    
    public Storage.Status getStatus()
    {
        return status;
    }

    public void setStatus(Storage.Status status)
    {
        this.status = status;
    }
}
