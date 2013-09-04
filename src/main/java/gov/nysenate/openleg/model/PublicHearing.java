package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

public class PublicHearing extends BaseObject
{
    public ArrayList<String> committees;
    public String title;
    public String location;
    public Date timeStamp;

    public ArrayList<Person> presidingSenators;
    public ArrayList<Person> presentSenators;
    public ArrayList<Person> presentAssemblyPersons;
    public ArrayList<Person> speakers;

    public TreeMap<Integer, String> pages;

    public PublicHearing() {
        committees = new ArrayList<String>();
        presidingSenators = new ArrayList<Person>();
        presentSenators = new ArrayList<Person>();
        presentAssemblyPersons = new ArrayList<Person>();
        speakers = new ArrayList<Person>();

        pages = new TreeMap<Integer, String>();
    }

    /**
     * The object type of the hearing.
     */
    public String getOtype()
    {
        return "publichearing";
    }

    public String getOid()
    {
        return "";
    }

    public ArrayList<String> getCommittees() {
        return committees;
    }
    public String getTitle() {
        return title;
    }
    public String getLocation() {
        return location;
    }
    public Date getTimeStamp() {
        return timeStamp;
    }
    public ArrayList<Person> getPresidingSenators() {
        return presidingSenators;
    }
    public ArrayList<Person> getPresentSenators() {
        return presentSenators;
    }
    public ArrayList<Person> getPresentAssemblyPersons() {
        return presentAssemblyPersons;
    }
    public ArrayList<Person> getSpeakers() {
        return speakers;
    }
    public TreeMap<Integer, String> getPages() {
        return pages;
    }
    public void setCommittees(ArrayList<String> committees) {
        this.committees = committees;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    public void setPresidingSenators(ArrayList<Person> presidingSenators) {
        this.presidingSenators = presidingSenators;
    }
    public void setPresentSenators(ArrayList<Person> presentSenators) {
        this.presentSenators = presentSenators;
    }
    public void setPresentAssemblyPersons(ArrayList<Person> presentAssemblyPersons) {
        this.presentAssemblyPersons = presentAssemblyPersons;
    }
    public void setSpeakers(ArrayList<Person> speakers) {
        this.speakers = speakers;
    }
    public void setPages(TreeMap<Integer, String> pages) {
        this.pages = pages;
    }

    public void addPerson(Person person, ArrayList<Person> persons) {
        persons.add(person);
    }

    public void addPage(int pageNumber, String pageText) {
        pages.put(pageNumber, pageText);
    }

    public int getYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimeStamp());
        return cal.get(Calendar.YEAR);
    }

    public static class Person {
        String name;
        String title;
        String committee;
        String organization;

        Integer page;
        Integer questions;

        public Person() {

        }

        public Person(String name, String title, String committee, String organization) {
            this.name = name;
            this.title = title;
            this.committee = committee;
            this.organization = organization;
        }

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        public String getCommittee() {
            return committee;
        }

        public String getOrganization() {
            return organization;
        }

        public Integer getPage() {
            return page;
        }

        public Integer getQuestions() {
            return questions;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setCommittee(String committee) {
            this.committee = committee;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public void setQuestions(int questions) {
            this.questions = questions;
        }
    }
}
