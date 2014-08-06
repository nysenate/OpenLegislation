package gov.nysenate.openleg.model.admin;

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

    private boolean currentAmendment;

    public ArrayList<String> actions;
    public ArrayList<String> cosponsors;
    public ArrayList<String> multisponsors;
    public ArrayList<String> amendments;

    public SpotCheckBill() {
        pages = year = 0;
        sameas = id = sponsor = title = summary = law = "";
        currentAmendment = false;
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

    public boolean isCurrentAmendment() {
        return currentAmendment;
    }

    public void setCurrentAmendment(boolean currentAmendment) {
        this.currentAmendment = currentAmendment;
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

    public ArrayList<String> getAmendments() {
        return amendments;
    }

    public void setMultisponsors(ArrayList<String> multisponsors) {
        this.multisponsors = multisponsors;
    }

    public void setAmendments(ArrayList<String> amendments) {
        this.amendments = amendments;
    }
}