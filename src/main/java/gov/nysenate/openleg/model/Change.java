package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.Storage;

import java.util.Date;


public class Change implements Comparable<Object>
{
    private String otype;
    private String oid;
    private Storage.Status status;
    private Date time;

    public Change() {

    }

    public Change(String oid, String otype, Storage.Status status, Date time)
    {
        this.setOid(oid);
        this.setOtype(otype);
        this.setTime(time);
        this.setStatus(status);
    }

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

    public Storage.Status getStatus()
    {
        return status;
    }

    public void setStatus(Storage.Status status)
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
        return this.getTime().compareTo(((Change) obj).getTime());
    }
}
