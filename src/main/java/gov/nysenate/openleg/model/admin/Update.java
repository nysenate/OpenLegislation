package gov.nysenate.openleg.model.admin;

import java.util.Date;

public class Update implements Comparable<Object>
{
    private String otype;
    private String oid;
    private Date time;
    private String status;

    public Update(){ }

    public String getOtype()
    {
        return otype;
    }
    public void setOtype(String otype)
    {
        this.otype = otype;
    }
    public String getOid()
    {
        return oid;
    }
    public void setOid(String oid)
    {
        this.oid = oid;
    }
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
    public Date getTime()
    {
        return time;
    }
    public void setTime(Date time)
    {
        this.time = time;
    }

    @Override
    public int compareTo(Object obj)
    {
        return this.getTime().compareTo(((Update) obj).getTime());
    }
}
