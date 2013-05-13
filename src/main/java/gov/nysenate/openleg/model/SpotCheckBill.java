package gov.nysenate.openleg.model;

import java.util.ArrayList;

public class SpotCheckBill {
    public int year;
    public int pages;

    public String id;
    public String law;
    public String title;
    public String sponsor;
    public String summary;
    public String sameas;

    public ArrayList<String> actions;
    public ArrayList<String> cosponsors;
    public ArrayList<String> multisponsors;
    public ArrayList<String> amendments;

    public SpotCheckBill() {
        pages = year = 0;
        sameas = id = sponsor = title = summary = law = "";
        cosponsors = new ArrayList<String>();
        multisponsors = new ArrayList<String>();
        actions = new ArrayList<String>();
        amendments = new ArrayList<String>();
    }
    

    public String getTitle()
    {
        return title;
    }

    public String setTitle(String title)
    {
        this.title = title;
        return title;
    }

    public String getSummary()
    {
        return summary;
    }

    public String setSummary(String summary)
    {
        this.summary = summary;
        return summary;
    }

    public String getSponsor()
    {
        return sponsor;
    }

    public String setSponsor(String sponsor)
    {
        this.sponsor = sponsor;
        return sponsor;
    }

    public ArrayList<String> getCosponsors()
    {
        return cosponsors;
    }

    public void setCosponsors(ArrayList<String> cosponsors)
    {
        this.cosponsors = cosponsors;
    }

    public ArrayList<String> getActions()
    {
        return actions;
    }

    public void setActions(ArrayList<String> actions)
    {
        this.actions = actions;
    }
    
}