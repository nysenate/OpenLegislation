package gov.nysenate.openleg.model;


import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Agenda extends BaseObject
{

    private String id;

    private int number;

    private int sessionYear;

    private int year;

    private List<Addendum> addendums;

    public Agenda(String id) {
        this.setId(id);
        addendums = new ArrayList<Addendum>();
    }

    public Agenda() {
        super();
        addendums = new ArrayList<Addendum>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Addendum> getAddendums() {
        return addendums;
    }

    public void setAddendums(List<Addendum> addendums) {
        this.addendums = addendums;
    }

    public int getSessionYear() {
        return sessionYear;
    }

    public void setSessionYear(int sessionYear) {
        this.sessionYear = sessionYear;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public Meeting getCommitteeMeeting(String id) {
        for(Addendum addendum:this.getAddendums()) {
            for(Meeting meeting:addendum.getMeetings()) {
                if(id.equals(meeting.getId())) {
                    return meeting;
                }
            }
        }
        return null;
    }

    public void removeCommitteeMeeting(Meeting meeting) {
        for(Addendum addendum:this.getAddendums()) {
            addendum.removeMeeting(meeting);
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Agenda)
        {
            if ( ((Agenda)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }

    @Override
    public String luceneOid() {
        return this.id;
    }

    @Override
    public String luceneOsearch() {
        return null;
    }

    @Override
    public String luceneOtype() {
        return "agenda";
    }

    @Override
    public String luceneSummary() {
        return null;
    }

    @Override
    public String luceneTitle() {
        return null;
    }
}
