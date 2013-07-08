package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class ReportError
{
    public enum FIELD {BILL_SUMMARY, BILL_TITLE, BILL_ACTION, BILL_SPONSOR, BILL_COSPONSOR, BILL_TEXT_PAGE};

    private int id;
    private String oid;
    private String field;

    private Date openedAt;
    private Date closedAt;
    private Collection<ReportObservation> observations = new ArrayList<ReportObservation>();

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public String getOid()
    {
        return oid;
    }
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    public String getField()
    {
        return field;
    }
    public void setField(String field)
    {
        this.field = field;
    }

    public Date getOpenedAt()
    {
        return openedAt;
    }
    public void setOpenedAt(Date openedAt)
    {
        this.openedAt = openedAt;
    }

    public Date getClosedAt()
    {
        return closedAt;
    }
    public void setClosedAt(Date closedAt)
    {
        this.closedAt = closedAt;
    }

    public Collection<ReportObservation> getObservations()
    {
        return observations;
    }
    public void setObservations(Collection<ReportObservation> observations)
    {
        this.observations = observations;
    }
}
