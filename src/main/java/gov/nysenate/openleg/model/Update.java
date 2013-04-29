package gov.nysenate.openleg.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Update implements Comparable<Object>
{
    private String otype;
    private String oid;
    private String date;
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
    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date = date;
    }
    
    /*
     * Returns a Date object represented by the objects date string.
     */
    public Date getDateObj()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(this.getDate());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public int compareTo(Object obj)
    {
        return this.getDateObj().compareTo(((Update) obj).getDateObj());
    }
}
