package gov.nysenate.openleg.model;

import java.util.Date;

public class Report
{
    private int id;
    private Date date;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String toString()
    {
        return getId()+": "+getDate();
    }
}
