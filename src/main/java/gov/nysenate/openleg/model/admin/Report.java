package gov.nysenate.openleg.model.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Report
{
    private int id;
    private Date time;
    private Collection<ReportObservation> observations = new ArrayList<ReportObservation>();
    private Collection<ReportError> openErrors = new ArrayList<ReportError>();
    private Collection<ReportObservation> closedObservations = new ArrayList<ReportObservation>();
    private Collection<ReportError> closedErrors = new ArrayList<ReportError>();
    private Collection<ReportError> newErrors = new ArrayList<ReportError>();

    public Report() {}

    public Report(int id, Date time) {
        this.id = id;
        this.time = time;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public String toString()
    {
        return getId()+": "+getTime();
    }

    public Collection<ReportObservation> getObservations()
    {
        return observations;
    }

    public void setObservations(Collection<ReportObservation> observations)
    {
        this.observations = observations;
    }

    public void addObservation(ReportObservation observation)
    {
        this.observations.add(observation);
    }

    public Collection<ReportError> getOpenErrors()
    {
        return openErrors;
    }

    public void setOpenErrors(Collection<ReportError> openErrors)
    {
        this.openErrors = openErrors;
    }

    public Collection<ReportError> getClosedErrors()
    {
        return closedErrors;
    }

    public void setClosedErrors(Collection<ReportError> closedErrors)
    {
        this.closedErrors = closedErrors;
    }

    public Collection<ReportError> getNewErrors()
    {
        return newErrors;
    }

    public void setNewErrors(Collection<ReportError> newErrors)
    {
        this.newErrors = newErrors;
    }

    public Collection<ReportObservation> getClosedObservations() {
        return closedObservations;
    }

    public void setClosedObservations(Collection<ReportObservation> closedObservations) {
        this.closedObservations = closedObservations;
    }
}
