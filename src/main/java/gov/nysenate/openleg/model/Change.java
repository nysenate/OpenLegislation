package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.Storage;

import java.util.Date;

/**
 *
 * @author GraylinKim
 */
public class Change implements Comparable<Object>
{
    /**
     * The type of the object that was changed.
     */
    private String otype;

    /**
     * The id of the object that was changed.
     */
    private String oid;

    /**
     * The new storage status of the object due to this change.
     */
    private Storage.Status status;

    /**
     * The time that the change occurred.
     */
    private Date time;

    /**
     * JavaBean Constructor
     */
    public Change()
    {

    }

    /**
     * Constructs a full Change object.
     * @param oid
     * @param otype
     * @param status
     * @param time
     */
    public Change(String oid, String otype, Storage.Status status, Date time)
    {
        this.setOid(oid);
        this.setOtype(otype);
        this.setTime(time);
        this.setStatus(status);
    }

    /**
     * @return - The type of the object that was changed.
     */
    public String getOtype()
    {
        return otype;
    }

    /**
     * @param otype - The new object type.
     */
    public void setOtype(String otype)
    {
        this.otype = otype;
    }

    /**
     * @return - The id of the object that was changed.
     */
    public String getOid()
    {
        return oid;
    }

    /**
     * @param oid - The new object id.
     */
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    /**
     * @return - The status of the object that was changed.
     */
    public Storage.Status getStatus()
    {
        return status;
    }

    /**
     * @param status - The new object status.
     */
    public void setStatus(Storage.Status status)
    {
        this.status = status;
    }

    /**
     * @return - The time that this object was changed.
     */
    public Date getTime()
    {
        return time;
    }

    /**
     * @param time - The new change time.
     */
    public void setTime(Date time)
    {
        this.time = time;
    }

    /**
     * Sort changes by time.
     */
    @Override
    public int compareTo(Object obj)
    {
        return this.getTime().compareTo(((Change) obj).getTime());
    }
}
