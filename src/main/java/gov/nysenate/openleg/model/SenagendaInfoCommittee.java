package gov.nysenate.openleg.model;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SenagendaInfoCommittee
{
    public String name;
    public String chair;
    public String location;
    public String meetDay;
    public Date meetDate;
    public String notes;
    public Map<String, Bill> bills;

    public SenagendaInfoCommittee()
    {
        bills = new HashMap<String, Bill>();
    }

    public SenagendaInfoCommittee(String name, String chair, String location, String notes, String meetDay, Date meetDate)
    {
        super();
        this.name = name;
        this.chair = chair;
        this.location = location;
        this.notes = notes;
        this.meetDay = meetDay;
        this.meetDate = meetDate;
    }
}

