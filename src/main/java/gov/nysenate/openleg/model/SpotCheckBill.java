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
}