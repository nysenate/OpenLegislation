package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SenagendaInfoAddendum
{
    public String id;
    public String weekOf;
    public Date pubDate;
    public Map<String, SenagendaInfoCommittee> committees;

    public SenagendaInfoAddendum()
    {
        committees = new HashMap<String, SenagendaInfoCommittee>();
    }

    public SenagendaInfoAddendum(String id, String weekOf, Date pubDate)
    {
        super();
        this.id = id;
        this.weekOf = weekOf;
        this.pubDate = pubDate;
    }
}
